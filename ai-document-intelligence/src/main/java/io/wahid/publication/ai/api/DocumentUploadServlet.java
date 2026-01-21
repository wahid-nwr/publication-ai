package io.wahid.publication.ai.api;

import io.wahid.publication.ai.exception.FileProcessingException;
import io.wahid.publication.ai.service.DocumentServiceExecutor;
import io.wahid.publication.ai.service.IngestionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig(
        fileSizeThreshold = 2 * 1024 * 1024,   // 2 MB
        maxFileSize = 200 * 1024 * 1024,       // 200 MB
        maxRequestSize = 210 * 1024 * 1024
)
public class DocumentUploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DocumentUploadServlet.class.getName());
    private static final Path UPLOAD_DIR = Path.of("/tmp/publication-upload");

    private final IngestionService ingestionService;

    public DocumentUploadServlet(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Files.createDirectories(UPLOAD_DIR);

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File is required");
            return;
        }

        String originalFileName = filePart.getSubmittedFileName();
        String jobId = UUID.randomUUID().toString();

        Path targetFile = UPLOAD_DIR.resolve(jobId + "-" + originalFileName);

        // TODO: trigger async processing (CSV parsing, hashing, blockchain, etc.)
        // jobService.submit(jobId, targetFile);
        DocumentServiceExecutor.submit(() -> {
            try {
                Instant start = Instant.now();
                LOGGER.log(Level.INFO,"Starting CSV parsing job: {0}", jobId);
//                JobRegistry.update(jobId, JobStatus.RUNNING);
                try (InputStream in = filePart.getInputStream()) {
//                    Files.copy(in, targetFile);
                    LOGGER.log(Level.INFO,"Initiating ingestion: {0}", jobId);
                    ingestionService.ingest(jobId, "csv", in);
                }
                Instant end = Instant.now();
                LOGGER.log(Level.INFO, "CSV parsing completed for job: {0}", jobId);
                LOGGER.log(Level.INFO, "Total time -> {0} seconds", Duration.between(start, end).toSeconds());
//                JobRegistry.update(jobId, JobStatus.SUCCESS);
            } catch (Exception e) {
//                JobRegistry.update(jobId, JobStatus.FAILED);
                LOGGER.log(Level.WARNING, "Error in document ingesting with job id, " + jobId + ": " + e.getMessage());
                throw new FileProcessingException("Error in CSV job " + jobId + ": " + e.getMessage(), e);
            } finally {
                try {
                    Thread.sleep(3000);
//                    JobRegistry.update(jobId, JobStatus.IDLE);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        resp.getWriter().write("""
            {
              "jobId": "%s",
              "status": "STARTED",
              "message": "Upload successful. Processing initiated."
            }
            """.formatted(jobId));
    }
}

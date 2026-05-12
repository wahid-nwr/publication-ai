package io.wahid.knowledge.api;

import io.wahid.knowledge.application.ApplicationContext;
import io.wahid.knowledge.infrastructure.storage.R2Client;
import io.wahid.knowledge.infrastructure.config.AppConfig;
import io.wahid.knowledge.application.exception.FileProcessingException;
import io.wahid.knowledge.application.domain.weather.insights.impl.AggregationOrchestrator;
import io.wahid.knowledge.application.core.ingestion.processing.CSVParser;
import io.wahid.knowledge.application.core.ingestion.processing.IngestionService;
import io.wahid.knowledge.application.domain.weather.insights.impl.UploadCheckTaskScheduler;
import io.wahid.knowledge.util.JobRegistry;
import io.wahid.knowledge.util.JobStatus;
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
    private static final String BUCKET = "documents";

    private final R2Client r2Client;
    private final UploadCheckTaskScheduler checkUploadScheduler;

    public DocumentUploadServlet(IngestionService ingestionService, R2Client r2Client) {
        this.r2Client = r2Client;
        AggregationOrchestrator aggregationOrchestrator = new AggregationOrchestrator(ingestionService);
        checkUploadScheduler = new UploadCheckTaskScheduler(aggregationOrchestrator);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String jobId = UUID.randomUUID().toString();
        Files.createDirectories(UPLOAD_DIR);

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File is required");
            return;
        }

        String originalFileName = filePart.getSubmittedFileName();
        String contentType = filePart.getContentType();
        String objectKey = "uploads/" + jobId + "/" + originalFileName;

        Path targetFile = UPLOAD_DIR.resolve(jobId + "-" + originalFileName);

        try (InputStream in = filePart.getInputStream()) {
            Instant start = Instant.now();
            JobRegistry.update(jobId, JobStatus.UPLOADING);
            Files.copy(in, targetFile);
            CSVParser parser = new CSVParser();
            boolean isValid = parser.isParsable(Files.newInputStream(targetFile), objectKey);
            if (isValid) {
                if (AppConfig.openaiEnabled()) {
                    r2Client.upload(
                            BUCKET,
                            objectKey,
                            contentType,
                            targetFile
                    );
                }
                ApplicationContext.setMetricValue("uploads", 1);
                checkUploadScheduler.startPeriodicTask(jobId, "csv", BUCKET, objectKey);
                Instant end = Instant.now();
                LOGGER.log(Level.INFO, "Total time taken -> {0}  seconds", Duration.between(start, end).toSeconds());
                JobRegistry.update(jobId, JobStatus.UPLOADED);
            }
        } catch (IOException e) {
            JobRegistry.update(jobId, JobStatus.FAILED);
            throw new FileProcessingException("Error in CSV job " + jobId, e);
        }

        /*DocumentServiceExecutor.submit(() -> {
            try {
                Instant start = Instant.now();
                LOGGER.log(Level.INFO, "Starting CSV parsing job: {0}", jobId);
                if (AppConfig.openaiEnabled()) {
                    ingestionService.ingestFromR2(
                            jobId,
                            "csv",
                            BUCKET,
                            objectKey
                    );
                } else {
                    ingestionService.ingest(jobId, "csv", targetFile);
                }
                LOGGER.log(Level.INFO, "CSV parsing completed for job: {0}", jobId);
                Instant end = Instant.now();
                LOGGER.log(Level.INFO, "Total time -> {0}s", Duration.between(start, end).toSeconds());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error in job " + jobId + ": " + e.getMessage(), e);
                throw new FileProcessingException("Error in CSV job " + jobId, e);
            }
        });*/

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        resp.getWriter().write("""
                {
                  "jobId": "%s",
                  "status": "%s",
                  "storage": "R2",
                  "objectKey": "%s",
                  "message": "Upload successful. Processing initiated."
                }
                """.formatted(jobId, JobRegistry.get(jobId).name(), objectKey));
    }
}

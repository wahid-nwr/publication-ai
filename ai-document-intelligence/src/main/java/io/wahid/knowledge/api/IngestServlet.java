package io.wahid.knowledge.api;

import io.wahid.knowledge.application.core.ingestion.processing.IngestionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@MultipartConfig
public class IngestServlet extends HttpServlet {

    private final IngestionService ingestionService;

    public IngestServlet(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        Part typePart = req.getPart("type");
        if (typePart == null) {
            resp.sendError(400, "Missing 'type' parameter");
            return;
        }

        String type = new String(typePart.getInputStream().readAllBytes()).trim();

        Part filePart = req.getPart("file");
        if (filePart == null) {
            resp.sendError(400, "Missing file");
            return;
        }

        String documentId = Optional.ofNullable(req.getPart("docId"))
                .map(p -> {
                    try {
                        return new String(p.getInputStream().readAllBytes()).trim();
                    } catch (IOException e) {
                        return null;
                    }
                })
                .orElse("doc-" + System.currentTimeMillis());

        try (InputStream in = filePart.getInputStream()) {
            try {
                // TODO parse jwt for tenant and workspace and pass them here
                ingestionService.ingest(documentId, documentId, documentId, type, in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        resp.setContentType("application/json");
        resp.getWriter().write("""
                {
                  "status": "accepted",
                  "documentId": "%s"
                }
                """.formatted(documentId));
    }
}


/*@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 50 * 1024 * 1024,
        maxRequestSize = 60 * 1024 * 1024
)
public class IngestServlet extends HttpServlet {

    private final IngestionService ingestionService;

    public IngestServlet(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Part typePart = req.getPart("type");
        String type = new String(typePart.getInputStream().readAllBytes()).trim();
        String documentId = Optional.ofNullable(req.getParameter("docId"))
                .orElse("doc-" + System.currentTimeMillis());

        if (type == null || type.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'type' parameter");
            return;
        }

        Part filePart = req.getPart("file");
        if (filePart == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file");
            return;
        }

        try (InputStream inputStream = filePart.getInputStream()) {
            ingestionService.ingest(documentId, type, inputStream);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        resp.getWriter().write("""
            {
              "status": "accepted",
              "documentId": "%s"
            }
            """.formatted(documentId));
    }
}*/

package io.wahid.publication.ai.api;

import io.wahid.publication.ai.service.IngestionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

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
            throws ServletException, IOException {

        String type = req.getParameter("type");
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
}

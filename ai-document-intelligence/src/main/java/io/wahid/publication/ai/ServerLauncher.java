package io.wahid.publication.ai;

import io.wahid.publication.ai.api.*;
import io.wahid.publication.ai.service.HealthService;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerLauncher {

/*    public static void main(String[] args) throws Exception {
        System.out.println("Inside serverlauncher");
        ApplicationContext applicationContext = new ApplicationContext();

        IngestServlet ingestServlet = new IngestServlet(applicationContext.ingestionService());

        QueryServlet queryServlet = new QueryServlet(applicationContext.queryService());

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setContextPath("/api");

        context.addServlet(
                new IngestServlet(new StubIngestionService()),
                "/ingest"
        );

        context.addServlet(
                new QueryServlet(new StubQueryService()),
                "/query"
        );

        server.setHandler(context);
        server.start();
        server.join();
    }*/

    public static void main(String[] args) throws Exception {
        System.out.println("Inside serverlauncher");

        ApplicationContext applicationContext = new ApplicationContext();

        IngestServlet ingestServlet = new IngestServlet(applicationContext.ingestionService());

        QueryServlet queryServlet = new QueryServlet(applicationContext.queryService());

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/api");

        ServletHolder ingestHolder = new ServletHolder(ingestServlet);

        Path uploadDir = Paths.get(System.getProperty("java.io.tmpdir"), "uploads");
        Files.createDirectories(uploadDir);
        ingestHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement(
                        uploadDir.toString(),             // location (null = temp dir)
                        50 * 1024 * 1024L,        // maxFileSize
                        60 * 1024 * 1024L,        // maxRequestSize
                        1024 * 1024               // fileSizeThreshold
                )
        );

        HealthService healthService = new HealthService(applicationContext.getOllamaClient(), applicationContext.getQdrantClient());
        HealthCheckServlet healthCheckServlet = new HealthCheckServlet(healthService);
        context.addServlet(new ServletHolder(healthCheckServlet), "/health");
        context.addServlet(new ServletHolder(new InfoServlet(applicationContext.getEmbeddingClient())), "/info");
        ReadyServlet readyServlet = new ReadyServlet(applicationContext.getOllamaClient(), applicationContext.getQdrantClient());
        context.addServlet(new ServletHolder(readyServlet), "/ready");
        context.addServlet(new ServletHolder(queryServlet), "/query");
        context.addServlet(ingestHolder, "/ingest");

        server.setHandler(context);

        server.start();
        server.join(); // ✔ correct and required
    }

}

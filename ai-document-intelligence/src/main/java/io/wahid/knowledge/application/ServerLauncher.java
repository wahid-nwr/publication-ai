package io.wahid.knowledge.application;

import io.wahid.knowledge.api.DocumentUploadServlet;
import io.wahid.knowledge.api.HealthCheckServlet;
import io.wahid.knowledge.api.InfoServlet;
import io.wahid.knowledge.api.IngestServlet;
import io.wahid.knowledge.api.LoginServlet;
import io.wahid.knowledge.api.QueryServlet;
import io.wahid.knowledge.api.ReadyServlet;
import io.wahid.knowledge.infrastructure.config.AppConfig;
import io.wahid.knowledge.application.exception.GlobalExceptionFilter;
import io.wahid.knowledge.security.JwtConfig;
import io.wahid.knowledge.security.JwtFilter;
import io.wahid.knowledge.infrastructure.health.HealthService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.logging.Logger;

public class ServerLauncher {
    private static final Logger LOGGER = Logger.getLogger(ServerLauncher.class.getName());

    public static void main(String[] args) throws Exception {
        LOGGER.info("Initiating Serverlauncher");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");

        ApplicationContext appContext = new ApplicationContext();

        IngestServlet ingestServlet = new IngestServlet(appContext.ingestionService());

        QueryServlet queryServlet = new QueryServlet(appContext.queryService());

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Server server = new Server(new InetSocketAddress("0.0.0.0", port));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder ingestHolder = new ServletHolder(ingestServlet);

        Path uploadDir = Paths.get(System.getProperty("java.io.tmpdir"), "uploads");
        Files.createDirectories(uploadDir);
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                uploadDir.toString(),             // location (null = temp dir)
                50 * 1024 * 1024L,        // maxFileSize
                60 * 1024 * 1024L,        // maxRequestSize
                1024 * 1024               // fileSizeThreshold
        );
        ingestHolder.getRegistration().setMultipartConfig(multipartConfig);

        FilterHolder exceptionFilterHolder = new FilterHolder(new GlobalExceptionFilter());
        context.addFilter(exceptionFilterHolder, "/*", EnumSet.of(
                DispatcherType.REQUEST,
                DispatcherType.ASYNC,
                DispatcherType.ERROR
        ));

        DocumentUploadServlet uploadServlet = new DocumentUploadServlet(appContext.ingestionService(), appContext.getR2Client());

        HealthService healthService = new HealthService(appContext.getLLMClient(), appContext.getQdrantClient());
        HealthCheckServlet healthCheckServlet = new HealthCheckServlet(healthService);
        context.addServlet(new ServletHolder(new LoginServlet()), "/auth/login");
        context.addServlet(new ServletHolder(healthCheckServlet), "/api/health");
        context.addServlet(new ServletHolder(new InfoServlet(appContext.getEmbeddingClient())), "/api/info");
        ReadyServlet readyServlet = new ReadyServlet(appContext.getLLMClient(), appContext.getEmbeddingClient(), appContext.getQdrantClient());
        context.addServlet(new ServletHolder(readyServlet), "/api/ready");
        context.addServlet(new ServletHolder(queryServlet), "/api/query");
        context.addServlet(ingestHolder, "/api/ingest");
        ServletHolder uploadHolder = new ServletHolder(uploadServlet);
        uploadHolder.getRegistration().setMultipartConfig(multipartConfig);
        context.addServlet(uploadHolder, "/api/document/upload");

        JwtConfig cfg = new JwtConfig(
                AppConfig.jwksUri(),                  // https://host/realms/realm
                AppConfig.issuer(),
                AppConfig.audience()                 // your client id
        );

        System.out.println("AppConfig.issuer()--->" + AppConfig.issuer());

        FilterHolder jwtFilterHolder = new FilterHolder(new JwtFilter(cfg));
        context.addFilter(jwtFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);

        server.start();
        server.join(); // ✔ correct and required
        LOGGER.info("RAG server initialized!");
    }

}

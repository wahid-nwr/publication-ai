package io.wahid.publication.ai;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import io.wahid.publication.ai.api.*;
import io.wahid.publication.ai.security.JwtConfig;
import io.wahid.publication.ai.security.JwtFilter;
import io.wahid.publication.ai.service.HealthService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.logging.Logger;

public class ServerLauncher {
    private static final Logger LOGGER = Logger.getLogger(ServerLauncher.class.getName());

    public static void main(String[] args) throws Exception {
        LOGGER.info("Initiating Serverlauncher");

        ApplicationContext applicationContext = new ApplicationContext();

        IngestServlet ingestServlet = new IngestServlet(applicationContext.ingestionService());

        QueryServlet queryServlet = new QueryServlet(applicationContext.queryService());

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

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
        context.addServlet(new ServletHolder(new LoginServlet()), "/auth/login");
        context.addServlet(new ServletHolder(healthCheckServlet), "/api/health");
        context.addServlet(new ServletHolder(new InfoServlet(applicationContext.getEmbeddingClient())), "/api/info");
        ReadyServlet readyServlet = new ReadyServlet(applicationContext.getOllamaClient(), applicationContext.getQdrantClient());
        context.addServlet(new ServletHolder(readyServlet), "/api/ready");
        context.addServlet(new ServletHolder(queryServlet), "/api/query");
        context.addServlet(ingestHolder, "/api/ingest");

        JwtConfig cfg = new JwtConfig(
                "https://www.googleapis.com/oauth2/v3/certs",
                "https://securetoken.google.com/alert-cursor-476219-s1"
        );
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(URI.create(cfg.getJwksUri()).toURL()).build();
        FilterHolder jwtFilterHolder = new FilterHolder(new JwtFilter(cfg, jwkSource));
        context.addFilter(jwtFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);

        server.start();
        server.join(); // ✔ correct and required
    }

}

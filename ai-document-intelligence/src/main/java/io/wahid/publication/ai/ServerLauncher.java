package io.wahid.publication.ai;

import io.wahid.publication.ai.api.IngestServlet;
import io.wahid.publication.ai.api.QueryServlet;
import io.wahid.publication.ai.service.impl.StubIngestionService;
import io.wahid.publication.ai.service.impl.StubQueryService;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
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
    }
}

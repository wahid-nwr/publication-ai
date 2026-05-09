package io.wahid.knowledge.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalExceptionFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionFilter.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (HttpResponseWriteException e) {
            writeError((HttpServletResponse) response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "RESPONSE_WRITE_ERROR",
                    e.getMessage());
        } catch (IllegalArgumentException e) {
            writeError((HttpServletResponse) response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "BAD_REQUEST",
                    e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unhandled exception", e);
            writeError((HttpServletResponse) response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "INTERNAL_ERROR",
                    "Unexpected server error");
        }
    }

    private void writeError(
            HttpServletResponse resp,
            int status,
            String code,
            String message
    ) throws IOException {
        if (resp.isCommitted()) {
            return;
        }

        resp.resetBuffer();
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        mapper.writeValue(
                resp.getWriter(),
                new ErrorResponse(code, message)
        );
    }
}

package local.socialnetwork.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.UUID;
import java.util.Optional;

/**
 * Servlet filter that propagates a correlation ID through {@link MDC} for distributed tracing.
 *
 * <p>The ID is read from the {@code X-Correlation-Id} request header, or a new UUID is generated
 * when the header is absent. The resolved ID is written to the response and cleared from MDC after
 * the request completes.
 */
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var correlationId = Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .filter(h -> !h.isBlank())
                .orElse(UUID.randomUUID().toString());
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

}

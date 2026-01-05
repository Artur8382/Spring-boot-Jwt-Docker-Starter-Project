package mysite.com.real.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!httpRequest.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String rateLimitKey;
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            rateLimitKey = "rate_limit:user:" + email;
        } else {
            String clientIp = httpRequest.getRemoteAddr();
            rateLimitKey = "rate_limit:ip:" + clientIp;
        }

        Long count = redisTemplate.opsForValue().increment(rateLimitKey);

        if (count != null && count == 1) {
            // First request → start window
            redisTemplate.expire(rateLimitKey, WINDOW);
        }

        if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests. Try again later.");
            return;
        }

        chain.doFilter(request, response);
    }
}

package jbc.com.cn.yidianav.filter;

import jbc.com.cn.yidianav.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/public/") || path.startsWith("/api/auth/") || 
            path.equals("/") || path.startsWith("/admin") || path.startsWith("/assets/") ||
            path.startsWith("/uploads/") || path.startsWith("/web_tool-master/") ||
            path.startsWith("/api/admin/backup/download/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && !jwtUtil.isTokenExpired(token)) {
                request.setAttribute("username", username);
                request.setAttribute("userId", jwtUtil.extractUserId(token));
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid token\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Invalid token\"}");
        }
    }
}

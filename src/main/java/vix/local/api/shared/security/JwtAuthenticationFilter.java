package vix.local.api.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vix.local.api.shared.tenant.TenantContext;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            if (jwtUtil.validateToken(jwt)) {
                email = jwtUtil.extractEmail(jwt);
                String schemaTarget = jwtUtil.extractSchemaTarget(jwt);
                List<String> roles = jwtUtil.extractRoles(jwt);
                java.util.UUID deptId = jwtUtil.extractDeptId(jwt);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Set schema for multi-tenancy
                    if (schemaTarget != null && !schemaTarget.isEmpty()) {
                        TenantContext.setSchema(schemaTarget);
                    }
                    if (deptId != null) {
                        request.setAttribute("X-Department-Id", deptId);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore invalid token, will be handled by Spring Security
        }

        filterChain.doFilter(request, response);
    }
}

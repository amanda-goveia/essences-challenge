package org.example.essenceschallenge.app.infra.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.essenceschallenge.app.service.AuthService;
import org.example.essenceschallenge.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if(!isUserAuthenticated() && hasAuthHeader(authHeader)) {
                String token = extractTokenFrom(authHeader);
                String username = authService.extractUsernameFrom(token);
                if(authService.validateToken(token, username)) {
                    setAuthentication(username, request);
                }
            }
        } catch(Exception e) {
            throw new UnauthorizedException("Unauthorized");
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUserAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null && context.getAuthentication() != null
                && context.getAuthentication().isAuthenticated();
    }

    private boolean hasAuthHeader(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    private String extractTokenFrom(String header) {
        if(!header.isBlank()) {
            return header.substring(7);
        }
        throw new UnauthorizedException("invalid token");
    }

    private void setAuthentication(String username, HttpServletRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }
}

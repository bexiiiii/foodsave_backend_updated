package foodsave.com.foodsave.config;

import foodsave.com.foodsave.config.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication; // Correct import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    // Constructor for injecting JwtTokenProvider
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the token from the request header
        String token = jwtTokenProvider.resolveToken(request);

        // If the token exists and is valid, set the authentication
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token); // Use the correct Authentication class
            SecurityContextHolder.getContext().setAuthentication(authentication); // Set the authentication in the context
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

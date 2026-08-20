package app.news.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import app.news.backend.security.CustomUserDetailsService;
import app.news.backend.security.JwtService;
import app.news.backend.service.TokenRevocationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TokenRevocationService tokenRevocationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwtFromCookie(request);

        /*
         * No JWT means this request is simply unauthenticated.
         *
         * SecurityConfig will decide whether the endpoint is public
         * or requires authentication.
         */
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Don't overwrite an Authentication that has already been
         * established by another authentication mechanism.
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtService.extractUsername(jwt);

            if (email == null) {
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * Load the current user from the database.
             */
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            /*
             * Validate:
             * 1. Signature
             * 2. Expiration
             * 3. Token/user relationship
             */
            if (!jwtService.validateToken(jwt, userDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * Even if the JWT is cryptographically valid,
             * it may have been explicitly revoked during logout.
             */
            String jti = jwtService.extractJti(jwt);

            if (tokenRevocationService.isRevoked(jti)) {
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * JWT is valid and not revoked.
             *
             * Create Spring Security's Authentication object.
             */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            /*
             * This is what makes the current request authenticated.
             */
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {

            /*
             * Invalid/expired JWT should NOT become a 500.
             *
             * We simply don't authenticate this request.
             * If the endpoint is protected, Spring Security will
             * subsequently return 401.
             */
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
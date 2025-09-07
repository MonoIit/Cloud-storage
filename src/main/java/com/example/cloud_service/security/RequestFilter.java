package com.example.cloud_service.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;
import com.example.cloud_service.services.MyUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class RequestFilter extends OncePerRequestFilter {
    private final TokenUtil tokenUtil;
    private final MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/login") || path.equals("/ping")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = request.getHeader("auth-token");

        UserDetails userDetails = null;
        if (token != null) {
            try {
                String signature = tokenUtil.getSignature(token);
                userDetails = userDetailsService.loadUserBySignature(signature);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get Token");
            } catch (UsernameNotFoundException e) {
                System.out.println("User not found!");
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        } else {
            System.out.println("No token provided");
        }

        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }


        chain.doFilter(request, response);
    }
}

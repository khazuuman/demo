package com.example.demo.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/v1/api/banner",
            "/v1/api/branch",
            "/v1/api/area",
            "/v1/api/category",
            "/v1/api/login",
            "/v1/api/product",
            "/v1/api/add-to-cart",
            "/v1/api/cart",
            "/v1/api/create-order",
            "/v1/api/order/"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestURI = request.getRequestURI();

        if (EXCLUDED_PATHS.contains(requestURI) || requestURI.startsWith("/v1/api/order/") || requestURI.startsWith("/v1/api/delete-to-cart/") || requestURI.startsWith("/images/") || requestURI.startsWith("/v1/api/product/")) {
            chain.doFilter(request, response);
            return;
        }

        // Nếu không có JWT hoặc không hợp lệ, bỏ qua
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Xử lý JWT
        String token = header.substring(7);
        try {
            Authentication authentication = userAuthenticationProvider.validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            throw e;
        }

        chain.doFilter(request, response);
    }
}

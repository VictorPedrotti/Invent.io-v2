package com.invent.io.api_gateway.config.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserHeaderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof JwtAuthenticationToken jwtAuth && jwtAuth.isAuthenticated()) {
      Jwt jwt = jwtAuth.getToken();
      String userId = jwt.getClaimAsString("sub");

      HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
        @Override
        public String getHeader(String name) {
          if ("X-User-Id".equalsIgnoreCase(name)) {
            return userId;
          }
          return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
          List<String> names = Collections.list(super.getHeaderNames());
          if (!names.contains("X-User-Id")) {
            names.add("X-User-Id");
          }
          return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
          if ("X-User-Id".equalsIgnoreCase(name)) {
            return Collections.enumeration(List.of(userId));
          }
          return super.getHeaders(name);
        }
      };
      
      filterChain.doFilter(wrappedRequest, response);
      return;
    }

    filterChain.doFilter(request, response);
  }
}

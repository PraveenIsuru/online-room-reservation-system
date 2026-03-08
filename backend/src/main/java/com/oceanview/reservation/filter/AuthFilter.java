package com.oceanview.reservation.filter;

import com.oceanview.reservation.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        System.out.println("AuthFilter: path=" + path + " method=" + requestContext.getMethod());

        // Handle CORS preflight
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        // Skip auth for login and logout endpoints
        if (path.equals("/auth/login") || path.equals("auth/login") ||
            path.equals("/auth/logout") || path.equals("auth/logout") ||
            path.equals("hello")) {
            System.out.println("AuthFilter: Skipping auth for path=" + path);
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Missing or invalid Authorization header")
                .build());
            return;
        }

        String token = authHeader.substring(7);
        try {
            final io.jsonwebtoken.Claims claims = JwtUtil.validateToken(token);
            final String role = claims.get("role", String.class);
            final String username = claims.get("username", String.class);

            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String r) {
                    return role != null && role.equalsIgnoreCase(r);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getSecurityContext().isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid or expired token")
                .build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}

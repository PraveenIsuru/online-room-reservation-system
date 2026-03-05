package com.oceanview.reservation.api;

import com.oceanview.reservation.service.AuthService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService = new AuthService();

    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Username and password are required"))
                .build();
        }

        return authService.login(username, password)
            .map(token -> {
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return Response.ok(response).build();
            })
            .orElseGet(() -> Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", "Invalid credentials"))
                .build());
    }

    @POST
    @Path("/logout")
    public Response logout() {
        // In JWT, logout is typically handled on the client side by discarding the token.
        // We can just return a success message.
        return Response.ok(Map.of("message", "Logged out successfully")).build();
    }
}

package com.oceanview.reservation.api;

import com.oceanview.reservation.model.Guest;
import com.oceanview.reservation.service.GuestService;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/guests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestResource {

    private final GuestService guestService = new GuestService();

    @GET
    public List<Guest> getGuests() {
        return guestService.getAllGuests();
    }

    @GET
    @Path("/{id}")
    public Response getGuestById(@PathParam("id") int id) {
        Guest guest = guestService.getGuestById(id);
        if (guest == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(guest).build();
    }

    @POST
    public Response createGuest(Guest guest, @Context HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        String ipAddress = request.getRemoteAddr();
        
        Guest created = guestService.createGuest(guest, userId, ipAddress);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateGuest(@PathParam("id") int id, Guest guest, @Context HttpServletRequest request) {
        guest.setGuestId(id);
        Integer userId = getUserIdFromToken(request);
        String ipAddress = request.getRemoteAddr();

        boolean updated = guestService.updateGuest(guest, userId, ipAddress);
        if (updated) {
            return Response.ok(guest).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private Integer getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Claims claims = JwtUtil.validateToken(token);
                return Integer.parseInt(claims.getSubject());
            } catch (Exception e) {
                // Ignore or log
            }
        }
        return null;
    }
}

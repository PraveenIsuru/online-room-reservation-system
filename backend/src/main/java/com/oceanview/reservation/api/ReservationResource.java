package com.oceanview.reservation.api;

import com.oceanview.reservation.dto.ReservationListDTO;
import com.oceanview.reservation.model.Reservation;
import com.oceanview.reservation.model.enums.ReservationStatus;
import com.oceanview.reservation.service.ReservationService;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final ReservationService reservationService = new ReservationService();

    @GET
    public Response listReservations(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("status") String statusStr,
            @QueryParam("search") String search,
            @QueryParam("guestId") Integer guestId) {

        ReservationStatus status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = ReservationStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status").build();
            }
        }

        List<ReservationListDTO> data = reservationService.listEnrichedReservations(offset, limit, status, search, guestId);
        int total = reservationService.countEnrichedReservations(status, search, guestId);

        return Response.ok(Map.of("data", data, "total", total)).build();
    }

    @GET
    @Path("/{id}")
    public Response getReservationById(@PathParam("id") int id) {
        ReservationListDTO res = reservationService.getEnrichedReservationById(id);
        if (res == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(Map.of("data", res)).build();
    }

    @GET
    @Path("/number/{number}")
    public Response getReservationByNumber(@PathParam("number") String number) {
        ReservationListDTO res = reservationService.getEnrichedReservationByNumber(number);
        if (res == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(Map.of("data", res)).build();
    }

    @POST
    public Response createReservation(Reservation reservation, @Context HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            String ipAddress = request.getRemoteAddr();
            Reservation created = reservationService.createReservation(reservation, userId, ipAddress);
            return Response.status(Response.Status.CREATED).entity(Map.of("data", created)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") int id, Map<String, String> statusUpdate, @Context HttpServletRequest request) {
        String statusStr = statusUpdate.get("status");
        if (statusStr == null) return Response.status(Response.Status.BAD_REQUEST).entity("Status is required").build();

        try {
            ReservationStatus status = ReservationStatus.valueOf(statusStr);
            Integer userId = getUserIdFromToken(request);
            String ipAddress = request.getRemoteAddr();
            
            boolean updated = reservationService.updateReservationStatus(id, status, userId, ipAddress);
            if (updated) return Response.ok().build();
            else return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status").build();
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

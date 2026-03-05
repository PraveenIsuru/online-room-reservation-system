package com.oceanview.reservation.api;

import com.oceanview.reservation.model.Room;
import com.oceanview.reservation.model.enums.RoomStatus;
import com.oceanview.reservation.model.enums.RoomType;
import com.oceanview.reservation.service.RoomService;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomService roomService = new RoomService();

    @GET
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }

    @GET
    @Path("/available")
    public Response getAvailableRooms(
            @QueryParam("checkIn") String checkInStr,
            @QueryParam("checkOut") String checkOutStr,
            @QueryParam("type") String typeStr) {
        
        if (checkInStr == null || checkOutStr == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("checkIn and checkOut dates are required")
                    .build();
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            RoomType type = (typeStr != null && !typeStr.isEmpty()) ? RoomType.valueOf(typeStr) : null;

            List<Room> availableRooms = roomService.getAvailableRooms(checkIn, checkOut, type);
            return Response.ok(availableRooms).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid parameters: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    public Response updateRoomStatus(@PathParam("id") int id, Map<String, String> statusUpdate, @Context HttpServletRequest request) {
        String statusStr = statusUpdate.get("status");
        if (statusStr == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Status is required").build();
        }

        try {
            RoomStatus status = RoomStatus.valueOf(statusStr);
            Integer userId = getUserIdFromToken(request);
            String ipAddress = request.getRemoteAddr();

            boolean updated = roomService.updateRoomStatus(id, status, userId, ipAddress);
            if (updated) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
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

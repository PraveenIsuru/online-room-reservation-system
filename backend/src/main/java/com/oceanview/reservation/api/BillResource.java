package com.oceanview.reservation.api;

import com.oceanview.reservation.model.Bill;
import com.oceanview.reservation.model.enums.PaymentMethod;
import com.oceanview.reservation.service.BillingService;
import com.oceanview.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillResource {

    private final BillingService billingService = new BillingService();

    @GET
    @Path("/reservation/{reservationId}")
    public Response getBillByReservation(@PathParam("reservationId") int reservationId) {
        Bill bill = billingService.getBillByReservationId(reservationId);
        if (bill == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(bill).build();
    }

    @PATCH
    @Path("/{id}/pay")
    public Response payBill(@PathParam("id") int id, Map<String, String> paymentData, @Context HttpServletRequest request) {
        String methodStr = paymentData.get("paymentMethod");
        if (methodStr == null) return Response.status(Response.Status.BAD_REQUEST).entity("Payment method is required").build();

        try {
            PaymentMethod method = PaymentMethod.valueOf(methodStr);
            Integer userId = getUserIdFromToken(request);
            String ipAddress = request.getRemoteAddr();

            boolean updated = billingService.payBill(id, method, userId, ipAddress);
            if (updated) return Response.ok().build();
            else return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid payment method").build();
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

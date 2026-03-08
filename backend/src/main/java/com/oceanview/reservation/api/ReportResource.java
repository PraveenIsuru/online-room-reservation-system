package com.oceanview.reservation.api;

import com.oceanview.reservation.dto.DashboardStats;
import com.oceanview.reservation.service.ReportService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Map;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class ReportResource {

    private final ReportService reportService = new ReportService();

    @GET
    @Path("/dashboard")
    public Response getDashboardStats() {
        DashboardStats stats = reportService.getDashboardStats();
        return Response.ok(Map.of("data", stats)).build();
    }

    @GET
    @Path("/revenue")
    public Response getRevenueReport(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        if (startDateStr == null || endDateStr == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("startDate and endDate are required").build();
        }
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            Map<String, Object> report = reportService.getRevenueReport(startDate, endDate);
            return Response.ok(Map.of("data", report)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date parameters: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/audit")
    public Response getAuditLogs() {
        return Response.ok(reportService.getAuditLogs()).build();
    }
}

package com.oceanview.reservation.api;

import com.oceanview.reservation.dto.DashboardStats;
import com.oceanview.reservation.service.ReportService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class ReportResource {

    private final ReportService reportService = new ReportService();

    @GET
    @Path("/dashboard")
    public Response getDashboardStats() {
        DashboardStats stats = reportService.getDashboardStats();
        return Response.ok(stats).build();
    }

    @GET
    @Path("/audit")
    public Response getAuditLogs() {
        return Response.ok(reportService.getAuditLogs()).build();
    }
}

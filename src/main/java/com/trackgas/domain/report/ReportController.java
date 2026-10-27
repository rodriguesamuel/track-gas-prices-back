package com.trackgas.domain.report;

import com.trackgas.domain.report.dto.ConsolidatedReportDto;
import com.trackgas.domain.report.dto.FuelReportDto;
import com.trackgas.domain.report.dto.MaintenanceReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/fuel")
    public FuelReportDto fuel(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.fuelReport(vehicleId, from, to);
    }

    @GetMapping("/maintenance")
    public MaintenanceReportDto maintenance(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.maintenanceReport(vehicleId, from, to);
    }

    @GetMapping("/consolidated")
    public ConsolidatedReportDto consolidated(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.consolidatedReport(vehicleId, from, to);
    }
}

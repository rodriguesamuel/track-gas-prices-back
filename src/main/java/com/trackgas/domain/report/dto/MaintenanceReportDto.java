package com.trackgas.domain.report.dto;

import com.trackgas.domain.maintenance.dto.ExpenseResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record MaintenanceReportDto(
        BigDecimal totalCost,
        Map<String, BigDecimal> costByCategory,
        List<ExpenseResponse> expenses
) {}

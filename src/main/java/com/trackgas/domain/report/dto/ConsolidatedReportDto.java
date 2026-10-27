package com.trackgas.domain.report.dto;

import java.math.BigDecimal;

public record ConsolidatedReportDto(
        BigDecimal totalFuelCost,
        BigDecimal totalMaintenanceCost,
        BigDecimal grandTotal,
        BigDecimal totalKm,
        BigDecimal allInCostPerKm
) {}

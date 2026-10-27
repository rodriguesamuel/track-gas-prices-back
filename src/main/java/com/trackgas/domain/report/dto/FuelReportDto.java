package com.trackgas.domain.report.dto;

import com.trackgas.domain.fuel.dto.FuelFillUpResponse;

import java.math.BigDecimal;
import java.util.List;

public record FuelReportDto(
        BigDecimal totalCost,
        BigDecimal totalLiters,
        BigDecimal avgPricePerLiter,
        BigDecimal avgKmPerLiter,
        BigDecimal costPerKm,
        BigDecimal totalKm,
        List<FuelFillUpResponse> fillUps
) {}

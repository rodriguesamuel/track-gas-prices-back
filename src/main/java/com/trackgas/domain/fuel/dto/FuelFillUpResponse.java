package com.trackgas.domain.fuel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FuelFillUpResponse(
        UUID id,
        UUID vehicleId,
        String vehicleLabel,
        LocalDate date,
        BigDecimal odometerKm,
        BigDecimal totalLiters,
        BigDecimal pricePerLiter,
        BigDecimal totalCost,
        String fuelType,
        Boolean fullTank,
        String notes,
        OffsetDateTime createdAt
) {}

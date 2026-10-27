package com.trackgas.domain.fuel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FuelFillUpRequest(
        @NotNull UUID vehicleId,
        @NotNull LocalDate date,
        @NotNull @DecimalMin("0.01") BigDecimal odometerKm,
        @NotNull @DecimalMin("0.001") BigDecimal totalLiters,
        @NotNull @DecimalMin("0.01") BigDecimal pricePerLiter,
        @NotBlank String fuelType,
        Boolean fullTank,
        String notes
) {}

package com.trackgas.domain.maintenance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        @NotNull UUID vehicleId,
        @NotNull LocalDate date,
        @NotBlank String category,
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal cost,
        BigDecimal odometerKm,
        String notes
) {}

package com.trackgas.domain.maintenance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID vehicleId,
        String vehicleLabel,
        LocalDate date,
        String category,
        String description,
        BigDecimal cost,
        BigDecimal odometerKm,
        String notes,
        OffsetDateTime createdAt
) {}

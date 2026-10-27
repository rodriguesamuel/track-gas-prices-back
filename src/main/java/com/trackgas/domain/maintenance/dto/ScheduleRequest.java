package com.trackgas.domain.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleRequest(
        @NotNull UUID vehicleId,
        @NotBlank String type,
        String description,
        BigDecimal nextKm,
        LocalDate nextDate,
        BigDecimal intervalKm,
        Short intervalMonths
) {}

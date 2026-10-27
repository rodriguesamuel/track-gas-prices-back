package com.trackgas.domain.maintenance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduleResponse(
        UUID id,
        UUID vehicleId,
        String vehicleLabel,
        String type,
        String description,
        BigDecimal nextKm,
        LocalDate nextDate,
        BigDecimal intervalKm,
        Short intervalMonths,
        OffsetDateTime createdAt
) {}

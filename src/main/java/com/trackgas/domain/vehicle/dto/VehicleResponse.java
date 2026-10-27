package com.trackgas.domain.vehicle.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String brand,
        String model,
        Short year,
        String plate,
        String fuelType,
        OffsetDateTime createdAt
) {}

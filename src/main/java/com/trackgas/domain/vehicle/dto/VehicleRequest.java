package com.trackgas.domain.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotBlank String brand,
        @NotBlank String model,
        Short year,
        String plate,
        @NotBlank String fuelType
) {}

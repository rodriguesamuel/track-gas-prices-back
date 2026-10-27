package com.trackgas.domain.fuel;

import com.trackgas.domain.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fuel_fillups")
@Getter
@Setter
@NoArgsConstructor
public class FuelFillUp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "odometer_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal odometerKm;

    @Column(name = "total_liters", nullable = false, precision = 10, scale = 3)
    private BigDecimal totalLiters;

    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 4)
    private BigDecimal pricePerLiter;

    @Column(name = "total_cost", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal totalCost;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @Column(name = "full_tank", nullable = false)
    private Boolean fullTank = true;

    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

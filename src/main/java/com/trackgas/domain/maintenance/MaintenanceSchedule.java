package com.trackgas.domain.maintenance;

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
@Table(name = "maintenance_schedules")
@Getter
@Setter
@NoArgsConstructor
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private String type;

    private String description;

    @Column(name = "next_km", precision = 10, scale = 2)
    private BigDecimal nextKm;

    @Column(name = "next_date")
    private LocalDate nextDate;

    @Column(name = "interval_km", precision = 10, scale = 2)
    private BigDecimal intervalKm;

    @Column(name = "interval_months")
    private Short intervalMonths;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

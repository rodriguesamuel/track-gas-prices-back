package com.trackgas.domain.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceExpenseRepository extends JpaRepository<MaintenanceExpense, UUID> {

    @Query("""
        SELECT e FROM MaintenanceExpense e JOIN FETCH e.vehicle v
        WHERE v.user.id = :userId
          AND (:hasVehicle = false OR v.id = :vehicleId)
          AND (:hasFrom = false OR e.date >= :from)
          AND (:hasTo = false OR e.date <= :to)
        ORDER BY e.date DESC
        """)
    List<MaintenanceExpense> findFiltered(
            @Param("userId") UUID userId,
            @Param("vehicleId") UUID vehicleId,
            @Param("hasVehicle") boolean hasVehicle,
            @Param("from") LocalDate from,
            @Param("hasFrom") boolean hasFrom,
            @Param("to") LocalDate to,
            @Param("hasTo") boolean hasTo
    );

    Optional<MaintenanceExpense> findByIdAndVehicleUserId(UUID id, UUID userId);
}

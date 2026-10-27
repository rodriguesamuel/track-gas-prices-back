package com.trackgas.domain.fuel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuelFillUpRepository extends JpaRepository<FuelFillUp, UUID> {

    @Query("""
        SELECT f FROM FuelFillUp f JOIN FETCH f.vehicle v
        WHERE v.user.id = :userId
          AND (:hasVehicle = false OR v.id = :vehicleId)
          AND (:hasFrom = false OR f.date >= :from)
          AND (:hasTo = false OR f.date <= :to)
        ORDER BY f.date DESC, f.createdAt DESC
        """)
    List<FuelFillUp> findFiltered(
            @Param("userId") UUID userId,
            @Param("vehicleId") UUID vehicleId,
            @Param("hasVehicle") boolean hasVehicle,
            @Param("from") LocalDate from,
            @Param("hasFrom") boolean hasFrom,
            @Param("to") LocalDate to,
            @Param("hasTo") boolean hasTo
    );

    Optional<FuelFillUp> findByIdAndVehicleUserId(UUID id, UUID userId);

    @Query("""
        SELECT f FROM FuelFillUp f
        WHERE f.vehicle.id = :vehicleId
          AND f.vehicle.user.id = :userId
          AND f.date < :before
        ORDER BY f.date DESC
        LIMIT 1
        """)
    Optional<FuelFillUp> findPreviousFillUp(
            @Param("vehicleId") UUID vehicleId,
            @Param("userId") UUID userId,
            @Param("before") LocalDate before
    );
}

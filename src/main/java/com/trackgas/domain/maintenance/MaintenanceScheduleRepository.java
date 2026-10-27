package com.trackgas.domain.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, UUID> {
    List<MaintenanceSchedule> findByVehicleUserIdAndVehicleIdOrderByTypeAsc(UUID userId, UUID vehicleId);
    List<MaintenanceSchedule> findByVehicleUserIdOrderByVehicleIdAscTypeAsc(UUID userId);
    Optional<MaintenanceSchedule> findByIdAndVehicleUserId(UUID id, UUID userId);
}

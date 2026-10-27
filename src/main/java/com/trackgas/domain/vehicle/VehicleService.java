package com.trackgas.domain.vehicle;

import com.trackgas.domain.vehicle.dto.VehicleRequest;
import com.trackgas.domain.vehicle.dto.VehicleResponse;
import com.trackgas.shared.SecurityUtils;
import com.trackgas.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<VehicleResponse> list() {
        return vehicleRepository.findByUserIdOrderByCreatedAtAsc(SecurityUtils.currentUserId())
                .stream().map(this::toResponse).toList();
    }

    public VehicleResponse findById(UUID id) {
        return toResponse(getOwned(id));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle v = new Vehicle();
        v.setUser(SecurityUtils.currentUser());
        apply(v, request);
        return toResponse(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        Vehicle v = getOwned(id);
        apply(v, request);
        return toResponse(vehicleRepository.save(v));
    }

    @Transactional
    public void delete(UUID id) {
        vehicleRepository.delete(getOwned(id));
    }

    private Vehicle getOwned(UUID id) {
        return vehicleRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
    }

    private void apply(Vehicle v, VehicleRequest r) {
        v.setBrand(r.brand());
        v.setModel(r.model());
        v.setYear(r.year());
        v.setPlate(r.plate());
        v.setFuelType(r.fuelType());
    }

    private VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(v.getId(), v.getBrand(), v.getModel(),
                v.getYear(), v.getPlate(), v.getFuelType(), v.getCreatedAt());
    }
}

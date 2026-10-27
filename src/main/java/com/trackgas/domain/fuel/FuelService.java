package com.trackgas.domain.fuel;

import com.trackgas.domain.fuel.dto.FuelFillUpRequest;
import com.trackgas.domain.fuel.dto.FuelFillUpResponse;
import com.trackgas.domain.vehicle.VehicleRepository;
import com.trackgas.shared.SecurityUtils;
import com.trackgas.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelService {

    private final FuelFillUpRepository fillUpRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<FuelFillUpResponse> list(UUID vehicleId, LocalDate from, LocalDate to) {
        UUID safeVehicleId = vehicleId != null ? vehicleId : new UUID(0, 0);
        LocalDate safeFrom = from != null ? from : LocalDate.MIN;
        LocalDate safeTo = to != null ? to : LocalDate.MAX;
        return fillUpRepository.findFiltered(
                SecurityUtils.currentUserId(),
                safeVehicleId, vehicleId != null,
                safeFrom, from != null,
                safeTo, to != null
        ).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FuelFillUpResponse findById(UUID id) {
        return toResponse(getOwned(id));
    }

    @Transactional
    public FuelFillUpResponse create(FuelFillUpRequest request) {
        var vehicle = vehicleRepository.findByIdAndUserId(request.vehicleId(), SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        FuelFillUp f = new FuelFillUp();
        f.setVehicle(vehicle);
        apply(f, request);
        return toResponse(fillUpRepository.save(f));
    }

    @Transactional
    public FuelFillUpResponse update(UUID id, FuelFillUpRequest request) {
        FuelFillUp f = getOwned(id);
        apply(f, request);
        return toResponse(fillUpRepository.save(f));
    }

    @Transactional
    public void delete(UUID id) {
        fillUpRepository.delete(getOwned(id));
    }

    private FuelFillUp getOwned(UUID id) {
        return fillUpRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Fill-up not found: " + id));
    }

    private void apply(FuelFillUp f, FuelFillUpRequest r) {
        f.setDate(r.date());
        f.setOdometerKm(r.odometerKm());
        f.setTotalLiters(r.totalLiters());
        f.setPricePerLiter(r.pricePerLiter());
        f.setFuelType(r.fuelType());
        f.setFullTank(r.fullTank() != null ? r.fullTank() : true);
        f.setNotes(r.notes());
    }

    private FuelFillUpResponse toResponse(FuelFillUp f) {
        String label = f.getVehicle().getBrand() + " " + f.getVehicle().getModel();
        return new FuelFillUpResponse(f.getId(), f.getVehicle().getId(), label,
                f.getDate(), f.getOdometerKm(), f.getTotalLiters(), f.getPricePerLiter(),
                f.getTotalCost(), f.getFuelType(), f.getFullTank(), f.getNotes(), f.getCreatedAt());
    }
}

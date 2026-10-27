package com.trackgas.domain.fuel;

import com.trackgas.domain.fuel.dto.FuelFillUpRequest;
import com.trackgas.domain.fuel.dto.FuelFillUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fuel-fillups")
@RequiredArgsConstructor
public class FuelController {

    private final FuelService fuelService;

    @GetMapping
    public List<FuelFillUpResponse> list(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return fuelService.list(vehicleId, from, to);
    }

    @GetMapping("/{id}")
    public FuelFillUpResponse findById(@PathVariable UUID id) {
        return fuelService.findById(id);
    }

    @PostMapping
    public ResponseEntity<FuelFillUpResponse> create(@Valid @RequestBody FuelFillUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelService.create(request));
    }

    @PutMapping("/{id}")
    public FuelFillUpResponse update(@PathVariable UUID id, @Valid @RequestBody FuelFillUpRequest request) {
        return fuelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fuelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

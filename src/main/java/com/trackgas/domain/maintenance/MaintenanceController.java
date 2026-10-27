package com.trackgas.domain.maintenance;

import com.trackgas.domain.maintenance.dto.*;
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
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // Schedules
    @GetMapping("/schedules")
    public List<ScheduleResponse> listSchedules(@RequestParam(required = false) UUID vehicleId) {
        return maintenanceService.listSchedules(vehicleId);
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.createSchedule(request));
    }

    @PutMapping("/schedules/{id}")
    public ScheduleResponse updateSchedule(@PathVariable UUID id, @Valid @RequestBody ScheduleRequest request) {
        return maintenanceService.updateSchedule(id, request);
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        maintenanceService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // Expenses
    @GetMapping("/expenses")
    public List<ExpenseResponse> listExpenses(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return maintenanceService.listExpenses(vehicleId, from, to);
    }

    @GetMapping("/expenses/{id}")
    public ExpenseResponse findExpenseById(@PathVariable UUID id) {
        return maintenanceService.findExpenseById(id);
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.createExpense(request));
    }

    @PutMapping("/expenses/{id}")
    public ExpenseResponse updateExpense(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        return maintenanceService.updateExpense(id, request);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        maintenanceService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

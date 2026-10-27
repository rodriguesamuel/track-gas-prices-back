package com.trackgas.domain.maintenance;

import com.trackgas.domain.maintenance.dto.*;
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
public class MaintenanceService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;

    // --- Schedules ---

    @Transactional(readOnly = true)
    public List<ScheduleResponse> listSchedules(UUID vehicleId) {
        UUID userId = SecurityUtils.currentUserId();
        List<MaintenanceSchedule> result = vehicleId != null
                ? scheduleRepository.findByVehicleUserIdAndVehicleIdOrderByTypeAsc(userId, vehicleId)
                : scheduleRepository.findByVehicleUserIdOrderByVehicleIdAscTypeAsc(userId);
        return result.stream().map(this::toScheduleResponse).toList();
    }

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        var vehicle = vehicleRepository.findByIdAndUserId(request.vehicleId(), SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        MaintenanceSchedule s = new MaintenanceSchedule();
        s.setVehicle(vehicle);
        applySchedule(s, request);
        return toScheduleResponse(scheduleRepository.save(s));
    }

    @Transactional
    public ScheduleResponse updateSchedule(UUID id, ScheduleRequest request) {
        MaintenanceSchedule s = scheduleRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));
        applySchedule(s, request);
        return toScheduleResponse(scheduleRepository.save(s));
    }

    @Transactional
    public void deleteSchedule(UUID id) {
        MaintenanceSchedule s = scheduleRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));
        scheduleRepository.delete(s);
    }

    // --- Expenses ---

    @Transactional(readOnly = true)
    public ExpenseResponse findExpenseById(UUID id) {
        MaintenanceExpense e = expenseRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        return toExpenseResponse(e);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(UUID vehicleId, LocalDate from, LocalDate to) {
        UUID safeVehicleId = vehicleId != null ? vehicleId : new UUID(0, 0);
        LocalDate safeFrom = from != null ? from : LocalDate.of(1900, 1, 1);
        LocalDate safeTo = to != null ? to : LocalDate.of(9999, 12, 31);
        return expenseRepository.findFiltered(
                SecurityUtils.currentUserId(),
                safeVehicleId, vehicleId != null,
                safeFrom, from != null,
                safeTo, to != null
        ).stream().map(this::toExpenseResponse).toList();
    }

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        var vehicle = vehicleRepository.findByIdAndUserId(request.vehicleId(), SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        MaintenanceExpense e = new MaintenanceExpense();
        e.setVehicle(vehicle);
        applyExpense(e, request);
        return toExpenseResponse(expenseRepository.save(e));
    }

    @Transactional
    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request) {
        MaintenanceExpense e = expenseRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        applyExpense(e, request);
        return toExpenseResponse(expenseRepository.save(e));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        MaintenanceExpense e = expenseRepository.findByIdAndVehicleUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        expenseRepository.delete(e);
    }

    // --- Helpers ---

    private void applySchedule(MaintenanceSchedule s, ScheduleRequest r) {
        s.setType(r.type());
        s.setDescription(r.description());
        s.setNextKm(r.nextKm());
        s.setNextDate(r.nextDate());
        s.setIntervalKm(r.intervalKm());
        s.setIntervalMonths(r.intervalMonths());
    }

    private void applyExpense(MaintenanceExpense e, ExpenseRequest r) {
        e.setDate(r.date());
        e.setCategory(r.category());
        e.setDescription(r.description());
        e.setCost(r.cost());
        e.setOdometerKm(r.odometerKm());
        e.setNotes(r.notes());
    }

    private ScheduleResponse toScheduleResponse(MaintenanceSchedule s) {
        String label = s.getVehicle().getBrand() + " " + s.getVehicle().getModel();
        return new ScheduleResponse(s.getId(), s.getVehicle().getId(), label,
                s.getType(), s.getDescription(), s.getNextKm(), s.getNextDate(),
                s.getIntervalKm(), s.getIntervalMonths(), s.getCreatedAt());
    }

    private ExpenseResponse toExpenseResponse(MaintenanceExpense e) {
        String label = e.getVehicle().getBrand() + " " + e.getVehicle().getModel();
        return new ExpenseResponse(e.getId(), e.getVehicle().getId(), label,
                e.getDate(), e.getCategory(), e.getDescription(), e.getCost(),
                e.getOdometerKm(), e.getNotes(), e.getCreatedAt());
    }
}

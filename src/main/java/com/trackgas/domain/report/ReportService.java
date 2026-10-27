package com.trackgas.domain.report;

import com.trackgas.domain.fuel.FuelFillUp;
import com.trackgas.domain.fuel.FuelFillUpRepository;
import com.trackgas.domain.fuel.dto.FuelFillUpResponse;
import com.trackgas.domain.maintenance.MaintenanceExpenseRepository;
import com.trackgas.domain.maintenance.dto.ExpenseResponse;
import com.trackgas.domain.report.dto.ConsolidatedReportDto;
import com.trackgas.domain.report.dto.FuelReportDto;
import com.trackgas.domain.report.dto.MaintenanceReportDto;
import com.trackgas.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final FuelFillUpRepository fillUpRepository;
    private final MaintenanceExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public FuelReportDto fuelReport(UUID vehicleId, LocalDate from, LocalDate to) {
        UUID userId = SecurityUtils.currentUserId();
        UUID safeVehicleId = vehicleId != null ? vehicleId : new UUID(0, 0);
        LocalDate safeFrom = from != null ? from : LocalDate.MIN;
        LocalDate safeTo = to != null ? to : LocalDate.MAX;
        List<FuelFillUp> fillUps = fillUpRepository.findFiltered(
                userId, safeVehicleId, vehicleId != null, safeFrom, from != null, safeTo, to != null);

        BigDecimal totalCost = fillUps.stream()
                .map(FuelFillUp::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiters = fillUps.stream()
                .map(FuelFillUp::getTotalLiters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgPricePerLiter = totalLiters.compareTo(BigDecimal.ZERO) > 0
                ? totalCost.divide(totalLiters, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // km/L: compute from consecutive full-tank fill-ups
        BigDecimal totalKm = computeTotalKm(fillUps);
        BigDecimal avgKmPerLiter = totalLiters.compareTo(BigDecimal.ZERO) > 0 && totalKm.compareTo(BigDecimal.ZERO) > 0
                ? totalKm.divide(totalLiters, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal costPerKm = totalKm.compareTo(BigDecimal.ZERO) > 0
                ? totalCost.divide(totalKm, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<FuelFillUpResponse> responses = fillUps.stream().map(f -> {
            String label = f.getVehicle().getBrand() + " " + f.getVehicle().getModel();
            return new FuelFillUpResponse(f.getId(), f.getVehicle().getId(), label,
                    f.getDate(), f.getOdometerKm(), f.getTotalLiters(), f.getPricePerLiter(),
                    f.getTotalCost(), f.getFuelType(), f.getFullTank(), f.getNotes(), f.getCreatedAt());
        }).toList();

        return new FuelReportDto(totalCost, totalLiters, avgPricePerLiter, avgKmPerLiter, costPerKm, totalKm, responses);
    }

    @Transactional(readOnly = true)
    public MaintenanceReportDto maintenanceReport(UUID vehicleId, LocalDate from, LocalDate to) {
        UUID userId = SecurityUtils.currentUserId();
        UUID safeVehicleId2 = vehicleId != null ? vehicleId : new UUID(0, 0);
        LocalDate safeFrom2 = from != null ? from : LocalDate.MIN;
        LocalDate safeTo2 = to != null ? to : LocalDate.MAX;
        var expenses = expenseRepository.findFiltered(
                userId, safeVehicleId2, vehicleId != null, safeFrom2, from != null, safeTo2, to != null);

        BigDecimal totalCost = expenses.stream()
                .map(e -> e.getCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory(),
                        Collectors.reducing(BigDecimal.ZERO, e -> e.getCost(), BigDecimal::add)
                ));

        List<ExpenseResponse> responses = expenses.stream().map(e -> {
            String label = e.getVehicle().getBrand() + " " + e.getVehicle().getModel();
            return new ExpenseResponse(e.getId(), e.getVehicle().getId(), label,
                    e.getDate(), e.getCategory(), e.getDescription(), e.getCost(),
                    e.getOdometerKm(), e.getNotes(), e.getCreatedAt());
        }).toList();

        return new MaintenanceReportDto(totalCost, byCategory, responses);
    }

    public ConsolidatedReportDto consolidatedReport(UUID vehicleId, LocalDate from, LocalDate to) {
        FuelReportDto fuel = fuelReport(vehicleId, from, to);
        MaintenanceReportDto maint = maintenanceReport(vehicleId, from, to);

        BigDecimal grandTotal = fuel.totalCost().add(maint.totalCost());
        BigDecimal totalKm = fuel.totalKm();
        BigDecimal allInCostPerKm = totalKm.compareTo(BigDecimal.ZERO) > 0
                ? grandTotal.divide(totalKm, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ConsolidatedReportDto(fuel.totalCost(), maint.totalCost(), grandTotal, totalKm, allInCostPerKm);
    }

    private BigDecimal computeTotalKm(List<FuelFillUp> fillUps) {
        // fillUps sorted DESC by date; reverse to ASC for consecutive diff
        List<FuelFillUp> asc = new ArrayList<>(fillUps);
        asc.sort((a, b) -> {
            int cmp = a.getDate().compareTo(b.getDate());
            return cmp != 0 ? cmp : a.getCreatedAt().compareTo(b.getCreatedAt());
        });

        BigDecimal totalKm = BigDecimal.ZERO;
        for (int i = 1; i < asc.size(); i++) {
            FuelFillUp prev = asc.get(i - 1);
            FuelFillUp curr = asc.get(i);
            if (Boolean.TRUE.equals(curr.getFullTank())) {
                BigDecimal diff = curr.getOdometerKm().subtract(prev.getOdometerKm());
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    totalKm = totalKm.add(diff);
                }
            }
        }
        return totalKm;
    }
}

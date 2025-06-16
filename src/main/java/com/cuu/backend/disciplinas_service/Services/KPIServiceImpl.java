package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiAgeDistribution;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiDebtorsQuantity;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiGenreQuantities;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiRevenuePerPeriodDistribution;
import com.cuu.backend.disciplinas_service.Repositories.FeeRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.KPIService;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Collectors;

@Service
public class KPIServiceImpl implements KPIService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private FeeRepo feeRepo;

    @Override
    public Long kpiUserGetQuantity() {
        return userRepo.countUsers();
    }

    @Override
    public KpiGenreQuantities kpiUserGetQuantityForeachGender() {
        return new KpiGenreQuantities(userRepo.countMaleUsers(), userRepo.countFemaleUsers());
    }

    @Override
    public List<KpiAgeDistribution> kpiUserGetAgeDistribution() {
        LocalDate now = LocalDate.now();

        Map<Integer, Long> ageDistribution = userRepo.findAll()
                .stream()
                .filter(user -> user.getBirthDate() != null)
                .collect(Collectors.groupingBy(
                        user -> Period.between(user.getBirthDate(), now).getYears(),
                        TreeMap::new,  // Usar TreeMap para orden automático por clave
                        Collectors.counting()
                ));

        List<KpiAgeDistribution> result = ageDistribution.entrySet().stream()
                .map(entry -> new KpiAgeDistribution(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public KpiDebtorsQuantity kpiFeeSocialGetDebtorsAndUpToDateQuantities() {
        return new KpiDebtorsQuantity(userRepo.countSocialDebtors(), userRepo.countSocialUpToDateUsers());
    }

    @Override
    public BigDecimal kpiFeeSocialGetRevenueAmount() {
        return feeRepo.sumPaidAmountsByFeeType(FeeType.SOCIAL);
    }

    @Override
    public List<KpiRevenuePerPeriodDistribution> kpiFeeSocialGetRevenuePerPeriod() {
        List<Object[]> results = feeRepo.findRevenueDistributionByPeriodFiltered(FeeType.SOCIAL);
        return mapResultsToDto(results);
    }

    @Override
    public KpiDebtorsQuantity kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(UUID disciplineId) {
        return new KpiDebtorsQuantity(userRepo.countDisciplineDebtors(disciplineId), userRepo.countDisciplineUpToDateUsers(disciplineId));
    }

    @Override
    public BigDecimal kpiFeeDisciplineGetRevenueAmount(UUID disciplineId) {
        return feeRepo.sumPaidAmountsByFeeTypeAndDisciplineId(FeeType.DISCIPLINE, disciplineId);
    }

    @Override
    public List<KpiRevenuePerPeriodDistribution> kpiFeeDisciplineGetRevenuePerPeriod(UUID disciplineId) {
        List<Object[]> results = feeRepo.findRevenueDistributionByPeriodAndDisciplineIdFiltered(
                FeeType.DISCIPLINE, disciplineId);
        return mapResultsToDto(results);
    }

    private List<KpiRevenuePerPeriodDistribution> mapResultsToDto(List<Object[]> results) {
        return results.stream()
                .map(result -> KpiRevenuePerPeriodDistribution.builder()
                        .period(((LocalDate)result[0]).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .revenue((BigDecimal) result[1]) // revenue
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long kpiDisciplineGetInscriptionsQuantity(UUID disciplineId) {
        return studentInscriptionRepo.countInscriptionsAmount(disciplineId);
    }

    @Override
    public KpiGenreQuantities kpiDisciplineGetInscriptionsQuantityForeachGender(UUID disciplineId) {
        return new KpiGenreQuantities(studentInscriptionRepo.countMaleUsers(disciplineId), studentInscriptionRepo.countFemaleUsers(disciplineId));

    }

    @Override
    public List<KpiAgeDistribution> kpiDisciplineGetAgeDistribution(UUID disciplineId) {
        LocalDate now = LocalDate.now();

        Map<Integer, Long> ageDistribution = studentInscriptionRepo.findDistinctStudentsByDisciplineId(disciplineId)
                .stream()
                .filter(user -> user.getBirthDate() != null)
                .collect(Collectors.groupingBy(
                        user -> Period.between(user.getBirthDate(), now).getYears(),
                        TreeMap::new,  // Usar TreeMap para orden automático por clave
                        Collectors.counting()
                ));

        List<KpiAgeDistribution> result = ageDistribution.entrySet().stream()
                .map(entry -> new KpiAgeDistribution(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return result;
    }
}

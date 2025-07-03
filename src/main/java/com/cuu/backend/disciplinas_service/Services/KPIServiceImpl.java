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
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private FeeRepo feeRepo;

    @Override
    public Long kpiUserGetQuantity(LocalDate start, LocalDate end) {
        return userRepo.countUsers(start, end);
    }

    @Override
    public KpiGenreQuantities kpiUserGetQuantityForeachGender(LocalDate start, LocalDate end) {
        return new KpiGenreQuantities(userRepo.countMaleUsers(start, end), userRepo.countFemaleUsers(start, end));
    }

    @Override
    public List<KpiAgeDistribution> kpiUserGetAgeDistribution(LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();

        Map<Integer, Long> ageDistribution = userRepo.findAllBetweenDates(start, end)
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
    public KpiDebtorsQuantity kpiFeeSocialGetDebtorsAndUpToDateQuantities(LocalDate start, LocalDate end) {
        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        return new KpiDebtorsQuantity(userRepo.countSocialDebtors(startYM, endYM), userRepo.countSocialUpToDateUsers(startYM, endYM));
    }

    @Override
    public BigDecimal kpiFeeSocialGetRevenueAmount(LocalDate start, LocalDate end) {
//        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
//        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        return feeRepo.sumPaidAmountsByFeeType(FeeType.SOCIAL, start, end);
    }

    @Override
    public List<KpiRevenuePerPeriodDistribution> kpiFeeSocialGetRevenuePerPeriod(LocalDate start, LocalDate end) {
//        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
//        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        List<Object[]> results = feeRepo.findRevenueDistributionByPeriodFiltered(FeeType.SOCIAL, start, end);
        return mapResultsToDto(results);
    }

    @Override
    public KpiDebtorsQuantity kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(UUID disciplineId, LocalDate start, LocalDate end) {
        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        return new KpiDebtorsQuantity(userRepo.countDisciplineDebtors(disciplineId, startYM, endYM), userRepo.countDisciplineUpToDateUsers(disciplineId, startYM, endYM));
    }

    @Override
    public BigDecimal kpiFeeDisciplineGetRevenueAmount(UUID disciplineId, LocalDate start, LocalDate end) {
//        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
//        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        return feeRepo.sumPaidAmountsByFeeTypeAndDisciplineId(FeeType.DISCIPLINE, disciplineId, start, end);
    }

    @Override
    public List<KpiRevenuePerPeriodDistribution> kpiFeeDisciplineGetRevenuePerPeriod(UUID disciplineId, LocalDate start, LocalDate end) {
//        YearMonth startYM = YearMonth.of(start.getYear(), start.getMonth());
//        YearMonth endYM = YearMonth.of(end.getYear(), end.getMonth());
        List<Object[]> results = feeRepo.findRevenueDistributionByPeriodAndDisciplineIdFiltered(
                FeeType.DISCIPLINE, disciplineId, start, end);
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
    public Long kpiDisciplineGetInscriptionsQuantity(UUID disciplineId, LocalDate start, LocalDate end) {
        return studentInscriptionRepo.countInscriptionsAmount(disciplineId, start, end);
    }

    @Override
    public KpiGenreQuantities kpiDisciplineGetInscriptionsQuantityForeachGender(UUID disciplineId, LocalDate start, LocalDate end) {
        return new KpiGenreQuantities(studentInscriptionRepo.countMaleUsers(disciplineId, start, end), studentInscriptionRepo.countFemaleUsers(disciplineId, start, end));

    }

    @Override
    public List<KpiAgeDistribution> kpiDisciplineGetAgeDistribution(UUID disciplineId, LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();

        Map<Integer, Long> ageDistribution = studentInscriptionRepo.findDistinctStudentsByDisciplineId(disciplineId, start, end)
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

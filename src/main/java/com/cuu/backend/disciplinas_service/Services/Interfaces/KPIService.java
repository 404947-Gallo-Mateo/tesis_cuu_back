package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiAgeDistribution;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiDebtorsQuantity;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiGenreQuantities;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiRevenuePerPeriodDistribution;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KPIService {
    //KPIs de Users
//    Long kpiUserGetQuantity();
//    KpiGenreQuantities kpiUserGetQuantityForeachGender();
//    List<KpiAgeDistribution> kpiUserGetAgeDistribution();
    Long kpiUserGetQuantity(LocalDate start, LocalDate end);
    KpiGenreQuantities kpiUserGetQuantityForeachGender(LocalDate start, LocalDate end);
    List<KpiAgeDistribution> kpiUserGetAgeDistribution(LocalDate start, LocalDate end);


    //KPIs de Fees SOCIAL
//    KpiDebtorsQuantity kpiFeeSocialGetDebtorsAndUpToDateQuantities();
//    BigDecimal kpiFeeSocialGetRevenueAmount();
//    List<KpiRevenuePerPeriodDistribution> kpiFeeSocialGetRevenuePerPeriod();
    KpiDebtorsQuantity kpiFeeSocialGetDebtorsAndUpToDateQuantities(LocalDate start, LocalDate end);
    BigDecimal kpiFeeSocialGetRevenueAmount(LocalDate start, LocalDate end);
    List<KpiRevenuePerPeriodDistribution> kpiFeeSocialGetRevenuePerPeriod(LocalDate start, LocalDate end);

    //KPIs de Fees DISCIPLINE
//    KpiDebtorsQuantity kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(UUID disciplineId);
//    BigDecimal kpiFeeDisciplineGetRevenueAmount(UUID disciplineId);
//    List<KpiRevenuePerPeriodDistribution> kpiFeeDisciplineGetRevenuePerPeriod(UUID disciplineId);
    KpiDebtorsQuantity kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(UUID disciplineId, LocalDate start, LocalDate end);
    BigDecimal kpiFeeDisciplineGetRevenueAmount(UUID disciplineId, LocalDate start, LocalDate end);
    List<KpiRevenuePerPeriodDistribution> kpiFeeDisciplineGetRevenuePerPeriod(UUID disciplineId, LocalDate start, LocalDate end);


    //KPIs de Discipline
//    Long kpiDisciplineGetInscriptionsQuantity(UUID disciplineId);
//    KpiGenreQuantities kpiDisciplineGetInscriptionsQuantityForeachGender(UUID disciplineId);
//    List<KpiAgeDistribution> kpiDisciplineGetAgeDistribution(UUID disciplineId);
    Long kpiDisciplineGetInscriptionsQuantity(UUID disciplineId, LocalDate start, LocalDate end);
    KpiGenreQuantities kpiDisciplineGetInscriptionsQuantityForeachGender(UUID disciplineId, LocalDate start, LocalDate end);
    List<KpiAgeDistribution> kpiDisciplineGetAgeDistribution(UUID disciplineId, LocalDate start, LocalDate end);
}

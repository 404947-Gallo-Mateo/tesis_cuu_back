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
    Long kpiUserGetQuantity();
    KpiGenreQuantities kpiUserGetQuantityForeachGender();
    List<KpiAgeDistribution> kpiUserGetAgeDistribution();

    //KPIs de Fees SOCIAL
    KpiDebtorsQuantity kpiFeeSocialGetDebtorsAndUpToDateQuantities();
    BigDecimal kpiFeeSocialGetRevenueAmount();
    List<KpiRevenuePerPeriodDistribution> kpiFeeSocialGetRevenuePerPeriod();

    //KPIs de Fees DISCIPLINE
    KpiDebtorsQuantity kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(UUID disciplineId);
    BigDecimal kpiFeeDisciplineGetRevenueAmount(UUID disciplineId);
    List<KpiRevenuePerPeriodDistribution> kpiFeeDisciplineGetRevenuePerPeriod(UUID disciplineId);


    //KPIs de Discipline
    Long kpiDisciplineGetInscriptionsQuantity(UUID disciplineId);
    KpiGenreQuantities kpiDisciplineGetInscriptionsQuantityForeachGender(UUID disciplineId);
    List<KpiAgeDistribution> kpiDisciplineGetAgeDistribution(UUID disciplineId);
}

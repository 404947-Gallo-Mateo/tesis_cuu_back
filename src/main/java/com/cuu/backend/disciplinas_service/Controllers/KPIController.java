package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiAgeDistribution;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiDebtorsQuantity;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiGenreQuantities;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiRevenuePerPeriodDistribution;
import com.cuu.backend.disciplinas_service.Services.Interfaces.KPIService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/kpi")
public class KPIController {

    @Autowired
    private KPIService kpiService;

    @GetMapping("/user/get-quantity")
    public ResponseEntity<Long> kpiUserGetQuantity(){

        return ResponseEntity.ok(kpiService.kpiUserGetQuantity());
    }

    @GetMapping("/user/get-quantity-for-each-gender")
    public ResponseEntity<KpiGenreQuantities> kpiUserGetQuantityForeachGender(){

        return ResponseEntity.ok(kpiService.kpiUserGetQuantityForeachGender());
    }

    @GetMapping("/user/get-age-distribution")
    public ResponseEntity<List<KpiAgeDistribution>> kpiUserGetAgeDistribution(){

        return ResponseEntity.ok(kpiService.kpiUserGetAgeDistribution());
    }

    //Fee SOCIAL
    @GetMapping("/fee-social/get-debtor-and-uptodate")
    public ResponseEntity<KpiDebtorsQuantity> kpiFeeSocialGetDebtorsAndUpToDateQuantities(){

        return ResponseEntity.ok(kpiService.kpiFeeSocialGetDebtorsAndUpToDateQuantities());
    }

    @GetMapping("/fee-social/get-revenue")
    public ResponseEntity<BigDecimal> kpiFeeSocialGetRevenueAmount(){

        return ResponseEntity.ok(kpiService.kpiFeeSocialGetRevenueAmount());
    }

    @GetMapping("/fee-social/get-revenue-per-period")
    public ResponseEntity<List<KpiRevenuePerPeriodDistribution>> kpiFeeSocialGetRevenuePerPeriod(){

        return ResponseEntity.ok(kpiService.kpiFeeSocialGetRevenuePerPeriod());
    }

    //Fee Discipline
    @GetMapping("/fee-discipline/get-debtor-and-uptodate/{disciplineId}")
    public ResponseEntity<KpiDebtorsQuantity> kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiFeeDisciplineGetDebtorsAndUpToDateQuantities(disciplineId));
    }

    @GetMapping("/fee-discipline/get-revenue/{disciplineId}")
    public ResponseEntity<BigDecimal> kpiFeeDisciplineGetRevenueAmount(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiFeeDisciplineGetRevenueAmount(disciplineId));
    }

    @GetMapping("/fee-discipline/get-revenue-per-period/{disciplineId}")
    public ResponseEntity<List<KpiRevenuePerPeriodDistribution>> kpiFeeDisciplineGetRevenuePerPeriod(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiFeeDisciplineGetRevenuePerPeriod(disciplineId));
    }

    // Discipline
    @GetMapping("/discipline/get-inscriptions/{disciplineId}")
    public ResponseEntity<Long> kpiDisciplineGetInscriptionsQuantity(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiDisciplineGetInscriptionsQuantity(disciplineId));
    }

    @GetMapping("/discipline/get-inscriptions-foreach-gender/{disciplineId}")
    public ResponseEntity<KpiGenreQuantities> kpiDisciplineGetInscriptionsQuantityForeachGender(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiDisciplineGetInscriptionsQuantityForeachGender(disciplineId));
    }

    @GetMapping("/discipline/get-age-distribution/{disciplineId}")
    public ResponseEntity<List<KpiAgeDistribution>> kpiDisciplineGetAgeDistribution(@PathVariable("disciplineId")UUID disciplineId){

        return ResponseEntity.ok(kpiService.kpiDisciplineGetAgeDistribution(disciplineId));
    }
}

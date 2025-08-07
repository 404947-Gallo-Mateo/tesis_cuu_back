package com.cuu.backend.disciplinas_service.Models.KPIs;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiRevenuePerPeriodDistribution {
    private String period; // yyyy-mm
    private BigDecimal revenue;
}

package com.cuu.backend.disciplinas_service.Models.KPIs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiDebtorsQuantity {
    protected Long debtorsAmount;
    protected Long upToDateAmount;
}

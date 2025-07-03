package com.cuu.backend.disciplinas_service.Models.KPIs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiAgeDistribution {
    private int age;
    private long count;
}

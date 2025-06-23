package com.cuu.backend.disciplinas_service.Models.KPIs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiGenreQuantities {
    private Long maleQuantity;
    private Long femaleQuantity;
}

package com.cuu.backend.disciplinas_service.Models.Entities.Embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgeRange {
    @Column(name = "min_age")
    private Integer minAge;
    @Column(name = "max_age")
    private Integer maxAge;
}

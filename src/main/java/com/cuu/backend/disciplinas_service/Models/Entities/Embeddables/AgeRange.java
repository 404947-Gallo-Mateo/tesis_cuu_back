package com.cuu.backend.disciplinas_service.Models.Entities.Embeddables;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgeRange {
    private Integer minAge;
    private Integer maxAge;
}

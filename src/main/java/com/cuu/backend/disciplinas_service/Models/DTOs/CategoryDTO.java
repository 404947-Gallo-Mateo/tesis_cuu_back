package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.AgeRange;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private UUID disciplineId;
    private String disciplineName;
    private Long availableSpaces;
    private AgeRange ageRange;
    private List<Schedule> schedule;
    @Enumerated(EnumType.STRING)
    private Genre allowedGenre;
}

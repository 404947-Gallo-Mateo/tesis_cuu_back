package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.AgeRange;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategoryDTO {

    private UUID id;

    //@Column(nullable = false)
    private String name;

    //@Column(length = 1500)
    private String description;

    //@Column(nullable = false)
    private BigDecimal monthlyFee;

    //@JoinColumn(name = "discipline_id", nullable = false)
    private DisciplineDTO discipline;

    private Long availablePlaces;

    private AgeRange ageRange;

    //@CollectionTable( joinColumns = @JoinColumn(name = "category_id") )
    private List<Schedule> schedule;
}

package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.CategorySummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.ICategorySummary;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.IDisciplineSummary;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DisciplineDTO implements IDisciplineSummary {
    private UUID id;
    private String name;
    private String description;
    private List<UserDTO> teachers;
    private List<ICategorySummary> categories;
}

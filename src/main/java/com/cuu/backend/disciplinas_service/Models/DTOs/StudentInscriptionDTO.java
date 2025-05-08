package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.CategorySummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.ICategorySummary;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.IDisciplineSummary;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StudentInscriptionDTO {
    private UserDTO student;
    private DisciplineSummaryDTO discipline;
    private CategorySummaryDTO category;
}

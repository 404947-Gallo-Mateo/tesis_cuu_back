package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.CategorySummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ExpandedStudentInscriptionDTO {
    @JsonProperty("isDebtor")
    private boolean isDebtor; // true si tiene al menos una Fee vencida
    private UserDTO student;
    private DisciplineSummaryDTO discipline;
    private CategorySummaryDTO category;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private List<FeeDTO> inscriptionFees;
    private Long paidFeesQuantity;
    private Long unPaidFeesQuantity;
    private Long latePaidFeesQuantity;
}

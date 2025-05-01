package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentInscriptionDTO {

    //@JoinColumn(nullable = false)
    private UserDTO student;

    //@JoinColumn(nullable = false)
    private DisciplineDTO discipline;

    //@JoinColumn(nullable = false)
    private CategoryDTO category;
}

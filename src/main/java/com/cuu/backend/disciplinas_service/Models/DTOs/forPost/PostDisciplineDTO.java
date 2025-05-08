package com.cuu.backend.disciplinas_service.Models.DTOs.forPost;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostDisciplineDTO {
    private String name;
    private String description;
    private List<UserDTO> teachers;
    private List<PostCategoryDTO> categories;
}

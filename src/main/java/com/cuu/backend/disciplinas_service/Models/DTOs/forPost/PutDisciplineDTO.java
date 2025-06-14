package com.cuu.backend.disciplinas_service.Models.DTOs.forPost;

import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PutDisciplineDTO {
    private UUID id;
    private String name;
    private String description;
    private List<String> teacherIds;//private List<UserDTO> teachers;
    private List<PutCategoryDTO> categories;
}

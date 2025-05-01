package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DisciplineDTO {

    private UUID id;

    //@Column(nullable = false, unique = true)
    private String name;

    //@Column(length = 1500)
    private String description;

    private List<UserDTO> teachers;

    private List<CategoryDTO> categories;
}

package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDTO registerCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> findAllByDisciplineId(UUID disciplineName);
}

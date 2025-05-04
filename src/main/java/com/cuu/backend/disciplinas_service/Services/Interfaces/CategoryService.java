package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostCategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDTO createCategory(PostCategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO);

    boolean deleteCategoryById(UUID categoryId);

    List<CategoryDTO> findAllByDisciplineId(UUID disciplineId);
}

package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CategoryDTO createCategory(PostCategoryDTO categoryDTO) {

        //todo validar

        Category newCategory = mapper.map(categoryDTO, Category.class);

        Category createdCategory = categoryRepo.save(newCategory);

        return mapper.map(createdCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Optional<Category> oldCategory = categoryRepo.findById(categoryDTO.getId());

        //todo validar


        Category updatedCategory = mapper.map(categoryDTO, Category.class);
        updatedCategory.setId(oldCategory.get().getId());

        Category savedCategory = categoryRepo.save(updatedCategory);

        return mapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public boolean deleteCategoryById(UUID categoryId) {
        //deberia encontrar 1 sola Category, caso contrario no hay nada q borrar
        Optional<Category> category = categoryRepo.findById(categoryId);

        if (category.isPresent()) {
            categoryRepo.delete(category.get());
            return true;
        }

        return false;        }

    @Override
    public List<CategoryDTO> findAllByDisciplineId(UUID disciplineId) {
        List<Category> categories = categoryRepo.findAllByDisciplineId(disciplineId);

        List<CategoryDTO> categoryDTOList = new ArrayList<>();

        for(Category c : categories){
            categoryDTOList.add(mapper.map(c, CategoryDTO.class));
        }

        return categoryDTOList;
    }
}

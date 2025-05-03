package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Services.Interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){

        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO){

        return ResponseEntity.ok(categoryService.updateCategory(categoryDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable UUID categoryId){

        return ResponseEntity.ok(categoryService.deleteCategoryById(categoryId));
    }

    @GetMapping("/find-all/by-discipline-id")
    public ResponseEntity<List<CategoryDTO>> findAllByDisciplineId(@RequestParam UUID disciplineId){

        return ResponseEntity.ok(categoryService.findAllByDisciplineId(disciplineId));
    }

}

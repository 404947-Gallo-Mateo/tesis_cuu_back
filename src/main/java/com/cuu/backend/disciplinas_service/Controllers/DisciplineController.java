package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutDisciplineDTO;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/discipline")
public class DisciplineController {

    @Autowired
    private DisciplineService disciplineService;

    @PostMapping("/create")
    public ResponseEntity<DisciplineDTO> createDiscipline(@RequestBody PostDisciplineDTO disciplineDTO){

        return ResponseEntity.ok(disciplineService.createDiscipline(disciplineDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<DisciplineDTO> updateDiscipline(@RequestBody PutDisciplineDTO disciplineDTO){

        return ResponseEntity.ok(disciplineService.updateDiscipline(disciplineDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteDisciplineById(@RequestParam UUID disciplineId){

        return ResponseEntity.ok(disciplineService.deleteDisciplineById(disciplineId));
    }

    @GetMapping("/find-one/by-name")
    public ResponseEntity<DisciplineDTO> findByName(@RequestParam String disciplineName){

        return ResponseEntity.ok(disciplineService.findByName(disciplineName));
    }

    @GetMapping("/find-one/by-id")
    public ResponseEntity<DisciplineDTO> findById(@RequestParam UUID disciplineId){

        return ResponseEntity.ok(disciplineService.findById(disciplineId));
    }

    @GetMapping("/find-all/by-teacher-keycloak-id")
    public ResponseEntity<List<DisciplineDTO>> findAllByTeacherKeycloakId(@RequestParam String teacherKeycloakId){

        return ResponseEntity.ok(disciplineService.findAllByTeacherKeycloakId(teacherKeycloakId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<DisciplineDTO>> getAll(){

        return ResponseEntity.ok(disciplineService.getAll());
    }


}

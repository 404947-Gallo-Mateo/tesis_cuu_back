package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/discipline")
public class DisciplineController {

    @Autowired
    private DisciplineService disciplineService;

    @PostMapping("/create")
    public ResponseEntity<DisciplineDTO> createDiscipline(@RequestBody DisciplineDTO disciplineDTO){

        return ResponseEntity.ok(disciplineService.createDiscipline(disciplineDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<DisciplineDTO> updateDiscipline(@RequestBody DisciplineDTO disciplineDTO){

        return ResponseEntity.ok(disciplineService.updateDiscipline(disciplineDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteDisciplineById(@PathVariable UUID disciplineId){

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
}

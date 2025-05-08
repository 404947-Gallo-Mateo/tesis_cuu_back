package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Controllers.Response.ApiResponse;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Services.Interfaces.StudentInscriptionService;
import org.apache.coyote.Response;
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
@RequestMapping("/student_inscription")
public class StudentInscriptionController {

    @Autowired
    private StudentInscriptionService studentInscriptionService;

    @PostMapping("/create")
    public ResponseEntity<StudentInscriptionDTO> createStudentInscription(@RequestBody StudentInscriptionDTO studentInscriptionDTO){

        return ResponseEntity.ok(studentInscriptionService.createStudentInscription(studentInscriptionDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<StudentInscriptionDTO> updateStudentInscription(@RequestBody StudentInscriptionDTO studentInscriptionDTO){

        return ResponseEntity.ok(studentInscriptionService.updateStudentInscription(studentInscriptionDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteStudentInscriptionByMultipleIDs(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam UUID categoryId){

        return ResponseEntity.ok(studentInscriptionService.deleteStudentInscriptionByMultipleIDs(studentKeycloakId, disciplineId, categoryId));
    }

    @GetMapping("/find-all/by-student-keycloak-id")
    public ResponseEntity<List<StudentInscriptionDTO>> findAllByStudentKeycloakId(@RequestParam String studentKeycloakId){

        return ResponseEntity.ok(studentInscriptionService.findAllByStudentKeycloakId(studentKeycloakId));
    }

    @GetMapping("/find-all/by-discipline-id")
    public ResponseEntity<List<StudentInscriptionDTO>> findAllByDisciplineId(@RequestParam UUID disciplineId){

        return ResponseEntity.ok(studentInscriptionService.findAllByDisciplineId(disciplineId));
    }

    @GetMapping("/find-all/by-category-id")
    public ResponseEntity<List<StudentInscriptionDTO>> findAllByCategoryId(@RequestParam UUID categoryId){

        return ResponseEntity.ok(studentInscriptionService.findAllByCategoryId(categoryId));
    }

    @GetMapping("/find-one/by-student-keycloak-id-and-discipline-id-and-category-id")
    public ResponseEntity<StudentInscriptionDTO> findByStudentKeycloakIdAndDisciplineIdAndCategoryId(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam UUID categoryId){

        return ResponseEntity.ok(studentInscriptionService.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentKeycloakId, disciplineId, categoryId));
    }

    @GetMapping("/find-one/by-student-keycloak-id-and-discipline-id")
    public ResponseEntity<StudentInscriptionDTO> findByStudentKeycloakIdAndDisciplineId(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId){

        return ResponseEntity.ok(studentInscriptionService.findByStudentKeycloakIdAndDisciplineId(studentKeycloakId, disciplineId));
    }
}

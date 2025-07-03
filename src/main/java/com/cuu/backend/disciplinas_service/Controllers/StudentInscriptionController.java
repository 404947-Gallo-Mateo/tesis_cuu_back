package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Controllers.Response.ApiResponse;
import com.cuu.backend.disciplinas_service.Models.DTOs.ExpandedStudentInscriptionDTO;
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
    public ResponseEntity<?> createStudentInscription(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam UUID categoryId){

        try {
            StudentInscriptionDTO dto = studentInscriptionService.createStudentInscription(studentKeycloakId, disciplineId, categoryId);
            return ResponseEntity.ok(dto);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStudentInscription(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam UUID categoryId){

        try {
            StudentInscriptionDTO dto = studentInscriptionService.updateStudentInscription(studentKeycloakId, disciplineId, categoryId);
            return ResponseEntity.ok(dto);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }    }

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

    @GetMapping("/find-all/by-discipline-id-with-fees")
    public ResponseEntity<List<ExpandedStudentInscriptionDTO>> findAllByDisciplineIdWithFees(@RequestParam UUID disciplineId){

        return ResponseEntity.ok(studentInscriptionService.findAllByDisciplineIdWithFees(disciplineId));
    }

    @GetMapping("/find-all/with-fees")
    public ResponseEntity<List<ExpandedStudentInscriptionDTO>> findAllWithFees(){

        return ResponseEntity.ok(studentInscriptionService.findAllWithFees());
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

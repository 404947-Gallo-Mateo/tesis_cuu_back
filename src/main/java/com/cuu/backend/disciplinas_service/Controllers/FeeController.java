package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Services.Interfaces.FeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/find-all")
    public ResponseEntity<List<FeeDTO>> findAll(){
        return ResponseEntity.ok(feeService.Test_GetAllFees());
    }
    @PutMapping("/update-paid-state")
    public ResponseEntity<?> UpdateFeePaidState(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam YearMonth period, @RequestParam Role userResponsibleRole){
        try {
            FeeDTO dto = feeService.UpdateFeePaidState(studentKeycloakId, disciplineId, period, userResponsibleRole);
            return ResponseEntity.ok(dto);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @GetMapping("/find-all/by-student-keycloak-id")
    public ResponseEntity<List<FeeDTO>> findAllByStudentKeycloakId(@RequestParam String studentKeycloakId){
        return ResponseEntity.ok(feeService.GetFeesByStudentKeycloakId(studentKeycloakId));
    }

    @GetMapping("/find-all/by-student-keycloak-id-and-discipline-id")
    public ResponseEntity<List<FeeDTO>> findAllByStudentKeycloakIdAndDisciplineId(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId){
        return ResponseEntity.ok(feeService.GetFeesByStudentKeycloakIdAndDisciplineId(studentKeycloakId, disciplineId));
    }

    @GetMapping("/find-all/by-fee-type")
    public ResponseEntity<List<FeeDTO>> findAllByFeeType(@RequestParam FeeType feeType){
        return ResponseEntity.ok(feeService.GetFeesByFeeType(feeType));
    }

    @GetMapping("/find-all/by-payer-email")
    public ResponseEntity<List<FeeDTO>> findAllByPayerEmail(@RequestParam String payerEmail){
        return ResponseEntity.ok(feeService.GetFeesByPayerEmail(payerEmail));
    }

    @GetMapping("/find-all/by-fee-type-and-student-keycloak-id")
    public ResponseEntity<List<FeeDTO>> findAllByFeeTypeAndStudentKeycloakId(@RequestParam FeeType feeType, @RequestParam String studentKeycloakId){
        return ResponseEntity.ok(feeService.findByFeeTypeAndUserKeycloakId(feeType, studentKeycloakId));
    }

    @GetMapping("/find-one/by-student-keycloak-id-and-discipline-id-and-period")
    public ResponseEntity<FeeDTO> findAllByStudentKeycloakIdAndDisciplineIdAndPeriod(@RequestParam String studentKeycloakId, @RequestParam UUID disciplineId, @RequestParam YearMonth period){
        return ResponseEntity.ok(feeService.GetFeesByStudentKeycloakIdAndDisciplineIdAndPeriod(studentKeycloakId, disciplineId, period));
    }

}

package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeService {

    List<FeeDTO> Test_GetAllFees();
    User CreateFeesForStudent(User student);
    FeeDTO UpdateFeePaidState(String userKeycloakId, FeeType feeType, UUID disciplineId, YearMonth period, Role userResponsibleRole);
    boolean MPUpdateFeePaidState(Long merchantOrderId, UUID feeId);
    boolean MPUpdateFeePaidStateToCancelled(Long merchantOrderId, UUID feeId);
    List<FeeDTO> GetFeesByStudentKeycloakId(String userKeycloakId);
    List<FeeDTO> GetFeesByStudentKeycloakIdAndDisciplineId(String userKeycloakId, UUID disciplineId);
    FeeDTO GetFeesByStudentKeycloakIdAndDisciplineIdAndPeriod(String userKeycloakId, UUID disciplineId, YearMonth period);
    List<FeeDTO> GetFeesByFeeType(FeeType feeType);
    List<FeeDTO> GetFeesByPayerEmail(String payerEmail);
    List<FeeDTO> findByFeeTypeAndUserKeycloakId(FeeType feeType, String userKeycloakId);
}

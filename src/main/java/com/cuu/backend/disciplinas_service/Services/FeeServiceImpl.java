package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.*;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.Enums.PaymentType;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.FeeRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.FeeService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private FeeRepo feeRepo;
    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private DisciplineRepo disciplineRepo;

    @Autowired
    private ComplexMapper complexMapper;

    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es"));

    @Override
    public List<FeeDTO> Test_GetAllFees() {
        List<Fee> feeList = feeRepo.findAll();

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;    }

    @Override
    @Transactional
    public User CreateFeesForStudent(User student) {

        if (student == null || student.getKeycloakId() == null) {
            throw new IllegalArgumentException("Estudiante o Keycloak ID no pueden ser nulos");
        }

        String studentKCID = student.getKeycloakId();

        List<StudentInscription> userInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(studentKCID);

        // si NO tiene inscripciones, NO se le generan cuotas (de ningun FeeType)
        if (!userInscriptions.isEmpty()){
            //generar Cuotas de FeeType SOCIAL
            GenerateSocialFeesForStudent(student, studentKCID, userInscriptions);
            GenerateDisciplineFeesForStudent(student, studentKCID, userInscriptions);
        }
        return student;
    }

    @Transactional
    private void GenerateSocialFeesForStudent(User student, String studentKCID, List<StudentInscription> studentInscriptions) {

        //periodo año-mes actual para comparar
        YearMonth currentPeriod = YearMonth.now();

        //Cuota SOCIAL mas reciente, si es q existe
        Optional<Fee> newestSocialFee = feeRepo.findLatestByFeeTypeAndStudentKeycloakId(FeeType.SOCIAL, studentKCID);

        //Inscripcion mas antigua, para saber desde q periodo (año-mes) crear Cuotas
        StudentInscription oldestInscription = studentInscriptions.stream()
                .min(Comparator.comparing(StudentInscription::getCreatedDate))
                .orElseThrow(() -> new IllegalStateException("El estudiante no tiene inscripciones"));


        //si NO existen Cuotas SOCIAL Ó la Cuota SOCIAL es anterior al periodo actual
        // se deben crear nuevas Cuotas SOCIAL
        if (newestSocialFee.isEmpty() || newestSocialFee.get().getPeriod().isBefore(currentPeriod)){
            //se crean TODAS las Cuotas, en base a la fecha de inscripcion de la StudentInscription mas antigua
            // (CreateFeesForStudent() SOLO genera cuotas (SOCIAL o DISCIPLNE) mientras el Student este inscrito en algo,
            //  caso contrario no se generan Cuotas, por eso usamos la fecha de inscripcion mas vieja)
            YearMonth oldestPeriod = YearMonth.from(oldestInscription.getCreatedDate());

            //si existe una Cuota SOCIAL, se crean nuevas Cuotas EXCLUYENDO el periodo mas viejo (esto porque ya existe su Cuota, newestSocialFee)
            if(newestSocialFee.isPresent()){
                //IF si el period de la newestSocialFee es MAYOR q el period de oldestInscription, se siguen generando Cuotas de corrido luego de newestSocialFee.
                // ELSE, si el period de oldestInscription es MAYOR, esto significa q el student estuvo inactivo (no estuvo inscripto en ninguna Discipline),
                // entonces NO se le generan Cuotas SOCIAL sobre los meses (periodo) que estuvo inactivo, se generan en base a la fecha de la inscripcion
                if (newestSocialFee.get().getPeriod().isAfter(oldestPeriod)){
                    //seguimos generando Cuotas SOCIAL luego de la ultima generada
                    oldestPeriod = newestSocialFee.get().getPeriod().plusMonths(1);

                } else if (newestSocialFee.get().getPeriod().isBefore(oldestPeriod)) {
                    oldestPeriod = YearMonth.from(oldestInscription.getCreatedDate());
                }
            }

            while (!oldestPeriod.isAfter(currentPeriod)){

                String description = "Cuota Social del Club - " + oldestPeriod.format(PERIOD_FORMATTER);

                Fee newFee = new Fee(null, FeeType.SOCIAL, BigDecimal.valueOf(5000), LocalDate.of(oldestPeriod.getYear(), oldestPeriod.getMonth().plus(1), 10),
                                    oldestPeriod, student, studentKCID, student.getEmail(), null, false,
                                    null, LocalDateTime.now(), description);

                Fee savedFee = feeRepo.save(newFee);

                student.getFees().add(savedFee);

                oldestPeriod = oldestPeriod.plusMonths(1);
            }
        }
        else if(newestSocialFee.get().getPeriod().isAfter(currentPeriod)){
            return;
        }
    }

    @Transactional
    private void GenerateDisciplineFeesForStudent(User student, String studentKCID, List<StudentInscription> studentInscriptions) {
        //periodo año-mes actual para comparar
        YearMonth currentPeriod = YearMonth.now();

        //recorre cada Inscription (osea tmb cada Discipline) del Alumno)
        for(StudentInscription si : studentInscriptions){
            //Cuota la DISCIPLINE particular mas reciente, si es q existe
            Optional<Fee> newestParticularDisciplineFee = feeRepo.findLatestByFeeTypeAndDisciplineIdAndStudentKeycloakId(FeeType.DISCIPLINE, si.getDiscipline().getId(), studentKCID);

            //fecha de Inscripcion a la Discipline, para saber desde q periodo (año-mes) crear Cuotas
            LocalDate inscriptionDate = si.getCreatedDate();

            //si NO existen Cuotas de la DISCIPLINE Ó la ultima Cuota de la DISCIPLINE es anterior al periodo actual
            // se deben crear nuevas Cuotas de la DISCIPLINE
            if (newestParticularDisciplineFee.isEmpty() || newestParticularDisciplineFee.get().getPeriod().isBefore(currentPeriod)){
                //se crean TODAS las Cuotas, en base a la fecha de inscripcion
                YearMonth oldestPeriod = YearMonth.from(inscriptionDate);

                //si existe una Cuota SOCIAL, se crean nuevas Cuotas EXCLUYENDO el periodo mas viejo (esto porque ya existe su Cuota, newestParticularDisciplineFee)
                if(newestParticularDisciplineFee.isPresent()){
                    //IF si el period de la newestParticularDisciplineFee es MAYOR q el period de oldestInscription, se siguen generando Cuotas de corrido luego de newestParticularDisciplineFee.
                    // ELSE, si el period de oldestInscription es MAYOR, esto significa q el student estuvo inactivo (no estuvo inscripto en esta Discipline),
                    // entonces NO se le generan Cuotas de la DISCIPLINE sobre los meses (periodo) que estuvo inactivo, se generan en base a la fecha de la inscripcion.
                    if (newestParticularDisciplineFee.get().getPeriod().isAfter(oldestPeriod)){
                        //seguimos generando Cuotas DISCIPLINE luego de la ultima generada
                        oldestPeriod = newestParticularDisciplineFee.get().getPeriod().plusMonths(1);

                    } else if (newestParticularDisciplineFee.get().getPeriod().isBefore(oldestPeriod)) {
                        oldestPeriod = YearMonth.from(inscriptionDate);
                    }
                }

                //se obtienen datos (ej: nombre, id, etc) de la Discipline sobre la cual se estan generando las Cuotas
                Optional<Discipline> optDiscipline = disciplineRepo.findById(si.getDiscipline().getId());
                String disciplineName = "{nombre_disciplina}";
                UUID disciplineId = new UUID(12, 12);
                BigDecimal categoryMonthlyFee = BigDecimal.valueOf(12000);

                if (optDiscipline.isPresent()){
                    disciplineName = optDiscipline.get().getName();
                    disciplineId = optDiscipline.get().getId();
                    // Buscar la categoría que coincida con el ID de la categoría de la inscripción
                    categoryMonthlyFee = optDiscipline.get().getCategories()
                            .stream()
                            .filter(category -> category.getId().equals(si.getCategory().getId()))
                            .findFirst()
                            .map(Category::getMonthlyFee)
                            .orElse(BigDecimal.valueOf(12000));
                }

                while (!oldestPeriod.isAfter(currentPeriod)){

                    String description = "Cuota de la Disciplina " + disciplineName + " - " + oldestPeriod.format(PERIOD_FORMATTER);

                    Fee newFee = new Fee(null, FeeType.DISCIPLINE, categoryMonthlyFee, LocalDate.of(oldestPeriod.getYear(), oldestPeriod.getMonth().plus(1), 10),
                            oldestPeriod, student, studentKCID, student.getEmail(), disciplineId, false,
                            null, LocalDateTime.now(), description);

                    Fee savedFee = feeRepo.save(newFee);

                    student.getFees().add(savedFee);

                    oldestPeriod = oldestPeriod.plusMonths(1);
                }
            }
            else if(newestParticularDisciplineFee.get().getPeriod().isAfter(currentPeriod)){
                return;
            }
        }
    }

    @Override
    public FeeDTO UpdateFeePaidState(String userKeycloakId, UUID disciplineId, YearMonth period, Role userResponsibleRole){

        Optional<Fee> feeOptional = feeRepo.findByUserKeycloakIdAndDisciplineIdAndPeriod(userKeycloakId, disciplineId, period);

        if (feeOptional.isEmpty()){
            throw new CustomException("No existe Cuota para el ID de usuario, ID de Disciplina y periodo indicado", HttpStatus.BAD_REQUEST);
        }

        Fee feeToUpdate = feeOptional.get();

        // SI es tipo Discipline pero el usuario q hace la solicitud NO es TEACHER, se cancela el proceso de marcar como pagada
        if (feeToUpdate.getFeeType().equals(FeeType.DISCIPLINE) && !userResponsibleRole.equals(Role.TEACHER)){
            throw new CustomException("Usted no es un Profesor, no puede modificar una Cuota de una Disciplina.", HttpStatus.FORBIDDEN);
        }

        if (feeToUpdate.getFeeType().equals(FeeType.SOCIAL) && !userResponsibleRole.equals(Role.SUPER_ADMIN_CUU) && !userResponsibleRole.equals(Role.ADMIN_CUU) ){
            throw new CustomException("Usted no es un Admin, no puede modificar una Cuota Social del Club.", HttpStatus.FORBIDDEN);
        }

        PaymentProof newPaymentProof = new PaymentProof(null, feeToUpdate, feeToUpdate.getUserKeycloakId(), LocalDateTime.now(), null, PaymentType.CASH, null, "approved", feeToUpdate.getPayerEmail());
        feeToUpdate.setPaid(true);
        feeToUpdate.setPaymentProof(newPaymentProof);

        Fee savedFee = feeRepo.save(feeToUpdate);

        return complexMapper.mapFeeEntityToFeeDTO(savedFee);
    }

    @Override
    public List<FeeDTO> GetFeesByStudentKeycloakId(String userKeycloakId) {
        List<Fee> feeList = feeRepo.findByUserKeycloakId(userKeycloakId);

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;
    }

    @Override
    public List<FeeDTO> GetFeesByStudentKeycloakIdAndDisciplineId(String userKeycloakId, UUID disciplineId) {
        List<Fee> feeList = feeRepo.findByUserKeycloakIdAndDisciplineId(userKeycloakId, disciplineId);

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;
    }

    @Override
    public FeeDTO GetFeesByStudentKeycloakIdAndDisciplineIdAndPeriod(String userKeycloakId, UUID disciplineId, YearMonth period) {
        Optional<Fee> optFee = feeRepo.findByUserKeycloakIdAndDisciplineIdAndPeriod(userKeycloakId, disciplineId, period);

        if(optFee.isEmpty()){
            throw new CustomException("No existe una Cuota para el Alumno indicado, en la Disciplina y período indicado.", HttpStatus.BAD_REQUEST);
        }

        return complexMapper.mapFeeEntityToFeeDTO(optFee.get());
    }

    @Override
    public List<FeeDTO> GetFeesByFeeType(FeeType feeType) {
        List<Fee> feeList = feeRepo.findByFeeType(feeType);

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;
    }

    @Override
    public List<FeeDTO> GetFeesByPayerEmail(String payerEmail) {
        List<Fee> feeList = feeRepo.findByPayerEmail(payerEmail);

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;
    }

    @Override
    public List<FeeDTO> findByFeeTypeAndUserKeycloakId(FeeType feeType, String userKeycloakId) {
        List<Fee> feeList = feeRepo.findByFeeTypeAndUserKeycloakId(feeType, userKeycloakId);

        List<FeeDTO> feeDTOList = new ArrayList<>();

        for(Fee f : feeList){
            feeDTOList.add(complexMapper.mapFeeEntityToFeeDTO(f));
        }

        return feeDTOList;
    }


}

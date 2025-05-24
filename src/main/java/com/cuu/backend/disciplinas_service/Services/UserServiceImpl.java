package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import com.cuu.backend.disciplinas_service.Services.RestClients.KeycloakAdminClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private KeycloakAdminClient keycloakAdminClient;
    @Autowired
    private ComplexMapper complexMapper;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public ExpandedUserDTO updateKeycloakUser(String keycloakId ,ExpandedUserDTO expandedUserDTO) {
        String token = keycloakAdminClient.getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // arma la estructura que espera Keycloak
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", expandedUserDTO.getFirstName());
        body.put("lastName", expandedUserDTO.getLastName());
        body.put("email", expandedUserDTO.getEmail());
        body.put("enabled", true);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("birthDate", expandedUserDTO.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        attributes.put("genre", expandedUserDTO.getGenre().toString().substring(0,1).toUpperCase()); //pasar como M o F
        body.put("attributes", attributes);

        try {
            ResponseEntity<Void> keycloakResponse = restTemplate.exchange(
                    "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + expandedUserDTO.getKeycloakId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(body, headers),
                    Void.class
            );

            if (keycloakResponse.getStatusCode().is2xxSuccessful()) {
                // Solo si se actualiza correctamente en Keycloak, se actualiza en la DB
                return this.updateKeycloakUserInLocalDb(expandedUserDTO);
            } else {
                throw new CustomException("Error al eliminar el usuario en Keycloak", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CustomException("Error al eliminar el usuario en Keycloak: " + e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new CustomException("Error inesperado al eliminar el usuario en Keycloak", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteKeycloakUserByKeycloakId(String userKeycloakId) {
        String token = keycloakAdminClient.getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String keycloakUrl = "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + userKeycloakId;

        try {
            ResponseEntity<Void> keycloakResponse = restTemplate.exchange(
                    keycloakUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class
            );

            if (keycloakResponse.getStatusCode().is2xxSuccessful()) {
                // Solo si se borra correctamente en Keycloak, se borra en tu DB
                return this.deleteKeycloakUserInLocalDb(userKeycloakId);
            } else {
                throw new CustomException("Error al eliminar el usuario en Keycloak", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CustomException("Error al eliminar el usuario en Keycloak: " + e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new CustomException("Error inesperado al eliminar el usuario en Keycloak", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ExpandedUserDTO updateKeycloakUserInLocalDb(ExpandedUserDTO expandedUserDTO){
        Optional<User> oldUser = userRepo.findByKeycloakId(expandedUserDTO.getKeycloakId());

        if (oldUser.isEmpty()){
            throw new CustomException("No se encontro el Usuario", HttpStatus.BAD_REQUEST);
        }

        User updatedUser = mapper.map(expandedUserDTO, User.class);
        updatedUser.setId(oldUser.get().getId());

        User saved = userRepo.save(updatedUser);

        ExpandedUserDTO expandedUserDTOSaved = mapper.map(saved, ExpandedUserDTO.class);

        expandedUserDTO.setStudentCategories(this.getAllStudentCategories(expandedUserDTOSaved.getKeycloakId()));

        return expandedUserDTOSaved;
    }


    private boolean deleteKeycloakUserInLocalDb(String userKeycloakId){
        Optional<User> user = userRepo.findByKeycloakId(userKeycloakId);

        if (user.isPresent()){
            userRepo.delete(user.get());
            return true;
        }

        return false;
    }

    @Override
    public List<ExpandedUserDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<ExpandedUserDTO> userDTOList = new ArrayList<>();

        for (User u : users){
            List<DisciplineSummaryDTO> teacherDisciplinesSummary = new ArrayList<>();
            List<CategoryDTO> studentCategoriesDto = new ArrayList<>();

            if (u.getRole().equals(Role.TEACHER) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
                teacherDisciplinesSummary = this.getAllTeacherDisciplines(u.getTeacherDisciplines());
            }
            if (u.getRole().equals(Role.STUDENT) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
                studentCategoriesDto = this.getAllStudentCategories(u.getKeycloakId());
            }

            ExpandedUserDTO expandedUserDTO = new ExpandedUserDTO(u.getKeycloakId(), u.getRole(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getBirthDate(), u.getGenre() , teacherDisciplinesSummary, studentCategoriesDto);

            userDTOList.add(expandedUserDTO);
        }

        return userDTOList;
    }

    @Override
    public ExpandedUserDTO syncUserFromKeycloak(Jwt jwt) {
        //datos (por defecto de keycloak) del User q se devuelve luego del login
        String keycloakId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        Role role = this.getRoleFromJwt(jwt);

        // datos personalizados (q agregue yo al User del Realm Club_Union_Unquillo) q devuelve keycloak luego del login
        String birthDateStr = jwt.getClaimAsString("birth_date");
        String genreStr = jwt.getClaimAsString("genre").toUpperCase();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
        Genre genre = genreStr.startsWith("M")? Genre.MALE : Genre.FEMALE ;

        User currentUser = userRepo.findByKeycloakId(keycloakId)
                //update si ya existe
                .map(existingUser -> {
                    existingUser.setKeycloakId(keycloakId);
                    existingUser.setRole(role);
                    existingUser.setUsername(username);
                    existingUser.setEmail(email);
                    existingUser.setFirstName(firstName != null ? firstName : username);
                    existingUser.setLastName(lastName != null ? lastName : "-");
                    existingUser.setBirthDate(birthDate);
                    existingUser.setGenre(genre);

                    //actualiza y devuelve el mismo User pero con los datos de la DB de este MS
                    return userRepo.save(existingUser);
                })
                // lo crea si NO existe
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setRole(role);
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName != null ? firstName : username);
                    newUser.setLastName(lastName != null ? lastName : "-");
                    newUser.setBirthDate(birthDate);
                    newUser.setGenre(genre);

                    //crea y devuelve el mismo User pero con los datos de la DB de este MS
                    return userRepo.save(newUser);
                });

        //agrega las categories donde esta inscripto el User
        ExpandedUserDTO expandedCurrentUser = mapper.map(currentUser, ExpandedUserDTO.class);
        expandedCurrentUser.setStudentCategories(this.getAllStudentCategories(keycloakId));

        return expandedCurrentUser;
    }

    private List<DisciplineSummaryDTO> getAllTeacherDisciplines(List<Discipline> teacherDisciplines){
        List<DisciplineSummaryDTO> teacherDisciplinesSummary = new ArrayList<>();

        for (Discipline d: teacherDisciplines){
            DisciplineSummaryDTO summary = new DisciplineSummaryDTO(d.getId(), d.getName());

            teacherDisciplinesSummary.add(summary);
        }

        return teacherDisciplinesSummary;
    }

    private List<CategoryDTO> getAllStudentCategories(String studentKeycloakId){
        List<CategoryDTO> studentCategories = new ArrayList<>();

        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(studentKeycloakId);

        for (StudentInscription si: studentInscriptions){
            Optional<Category> optCatEntity = categoryRepo.findById(si.getCategory().getId());

            optCatEntity.ifPresent(category -> studentCategories.add(mapper.map(category, CategoryDTO.class)));
        }

        return studentCategories;
    }

    private Role getRoleFromJwt(Jwt jwt){
        // Obtener los roles desde el claim realm_access.roles
        List<String> realmRoles = jwt.getClaim("realm_access") != null
                ? ((Map<String, List<String>>) jwt.getClaim("realm_access")).get("roles")
                : new ArrayList<>();

       if (realmRoles.contains("SUPER_ADMIN_CUU")){
            return Role.SUPER_ADMIN_CUU;
        }
        if (realmRoles.contains("ADMIN_CUU")){
            return Role.ADMIN_CUU;
        }
        if (realmRoles.contains("TEACHER")){
            return Role.TEACHER;
        }
        if (realmRoles.contains("STUDENT")){
            return Role.STUDENT;
        }

        throw new CustomException("el User NO tiene un ROLE valido", HttpStatus.BAD_REQUEST);

    }
}

package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Repositories.*;
import com.cuu.backend.disciplinas_service.Services.Interfaces.FeeService;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import com.cuu.backend.disciplinas_service.Services.RestClients.KeycloakAdminClient;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private DisciplineTeachersRepo disciplineTeachersRepo;
    @Autowired
    private FeeService feeService;

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
    @Transactional
    public ExpandedUserDTO updateKeycloakUser(String keycloakId, ExpandedUserDTO expandedUserDTO) {
        String token = keycloakAdminClient.getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Actualizar datos básicos del usuario en Keycloak
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", expandedUserDTO.getFirstName());
        body.put("lastName", expandedUserDTO.getLastName());
        body.put("email", expandedUserDTO.getEmail());
        body.put("enabled", true);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("birthDate", expandedUserDTO.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        attributes.put("genre", expandedUserDTO.getGenre().toString().substring(0, 1).toUpperCase()); // "M" o "F"
        body.put("attributes", attributes);

        try {
            // Actualizar datos básicos
            ResponseEntity<Void> keycloakResponse = restTemplate.exchange(
                    "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + expandedUserDTO.getKeycloakId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(body, headers),
                    Void.class
            );

            if (!keycloakResponse.getStatusCode().is2xxSuccessful()) {
                throw new CustomException("Error al actualizar el usuario en Keycloak", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 2. Actualizar el rol en Keycloak (solo si cambio el Role)
            //comparacion de Role
            Role currentUserOldRole = userRepo.findByKeycloakId(expandedUserDTO.getKeycloakId()).get().getRole();
            Role currentUserNewRole = expandedUserDTO.getRole();
            if (!currentUserOldRole.equals(currentUserNewRole)){
                updateKeycloakUserRole(expandedUserDTO.getKeycloakId(), expandedUserDTO.getRole(), token);
            }

            // 3. Actualizar en la base de datos local
            return this.updateKeycloakUserInLocalDb(expandedUserDTO);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CustomException("Error en Keycloak: " + e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new CustomException("Error inesperado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para actualizar el rol en Keycloak
    @Transactional
    private void updateKeycloakUserRole(String keycloakId, Role newRole, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // 1. Eliminar roles actuales (excepto el rol default)
            removeCurrentRoles(keycloakId, adminToken);

            // 2. Asignar nuevo rol
            String roleId = getKeycloakRoleId(newRole.name(), adminToken);
            String roleMappingUrl = "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + keycloakId + "/role-mappings/realm";

            List<Map<String, String>> rolesToAdd = List.of(
                    Map.of("id", roleId, "name", newRole.name())
            );

            restTemplate.exchange(
                    roleMappingUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(rolesToAdd, headers),
                    Void.class
            );
        } catch (Exception e) {
            throw new CustomException("Error al actualizar rol en Keycloak: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    private void removeCurrentRoles(String keycloakId, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        String roleMappingUrl = "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + keycloakId + "/role-mappings/realm";

        // Lista de roles que SÍ deben eliminarse
        Set<String> rolesToDelete = Set.of("STUDENT", "TEACHER", "SUPER_ADMIN_CUU", "ADMIN_CUU");

        // Obtener roles actuales del usuario
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                roleMappingUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            // Filtrar SOLO los roles que están en la lista rolesToDelete
            List<Map<String, Object>> rolesToRemove = response.getBody().stream()
                    .filter(role -> rolesToDelete.contains(role.get("name")))
                    .collect(Collectors.toList());

            // Eliminar solo los roles filtrados (si hay alguno)
            if (!rolesToRemove.isEmpty()) {
                restTemplate.exchange(
                        roleMappingUrl,
                        HttpMethod.DELETE,
                        new HttpEntity<>(rolesToRemove, headers),
                        Void.class
                );
                System.out.println("Roles eliminados: " + rolesToRemove.stream()
                        .map(role -> role.get("name").toString())
                        .collect(Collectors.joining(", ")));
            } else {
                System.out.println("No se encontraron roles para eliminar");
            }
        }
    }

    @Transactional
    // Método para obtener el ID del rol en Keycloak
    private String getKeycloakRoleId(String roleName, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://localhost:8080/admin/realms/Club_Union_Unquillo/roles/" + roleName,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null && response.getBody().containsKey("id")) {
                return response.getBody().get("id").toString();
            } else {
                throw new CustomException("Rol no encontrado en Keycloak: " + roleName, HttpStatus.NOT_FOUND);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new CustomException("Rol no existe en Keycloak: " + roleName, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
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

    @Transactional
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


    @Transactional
    private boolean deleteKeycloakUserInLocalDb(String userKeycloakId){
        Optional<User> user = userRepo.findByKeycloakId(userKeycloakId);

        if (user.isPresent()){

           studentInscriptionRepo.deleteByUserId(user.get().getId());
           disciplineRepo.deleteDisciplineTeacherUserRelations(user.get().getId());
           disciplineTeachersRepo.deleteTeacherUserDisciplineRelations(user.get().getId());

            userRepo.delete(user.get());
            return true;
        }

        return false;
    }

    @Override
    public List<ExpandedUserDTO> getAllUsers() {
        List<User> users = userRepo.getAllOrdered();
        List<ExpandedUserDTO> userDTOList = new ArrayList<>();

        for (User u : users){
            List<DisciplineSummaryDTO> teacherDisciplinesSummary = new ArrayList<>();
            List<CategoryDTO> studentCategoriesDto = new ArrayList<>();

            //if (u.getRole().equals(Role.TEACHER) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
                teacherDisciplinesSummary = this.getAllTeacherDisciplines(u.getTeacherDisciplines());
            //}
            //if (u.getRole().equals(Role.STUDENT) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
                studentCategoriesDto = this.getAllStudentCategories(u.getKeycloakId());
            //}

            ExpandedUserDTO expandedUserDTO = new ExpandedUserDTO(u.getKeycloakId(), u.getRole(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getBirthDate(), u.getGenre() , teacherDisciplinesSummary, studentCategoriesDto);

            userDTOList.add(expandedUserDTO);
        }

        return userDTOList;
    }

    @Override
    public List<ExpandedUserDTO> getAllUsersByRole(Role role) {
        List<User> users = userRepo.getAllByRoleOrdered(role);
        List<ExpandedUserDTO> userDTOList = new ArrayList<>();

        for (User u : users){
            List<DisciplineSummaryDTO> teacherDisciplinesSummary = new ArrayList<>();
            List<CategoryDTO> studentCategoriesDto = new ArrayList<>();

            //if (u.getRole().equals(Role.TEACHER) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
            teacherDisciplinesSummary = this.getAllTeacherDisciplines(u.getTeacherDisciplines());
            //}
            //if (u.getRole().equals(Role.STUDENT) || u.getRole().equals(Role.SUPER_ADMIN_CUU) || u.getRole().equals(Role.ADMIN_CUU)){
            studentCategoriesDto = this.getAllStudentCategories(u.getKeycloakId());
            //}

            ExpandedUserDTO expandedUserDTO = new ExpandedUserDTO(u.getKeycloakId(), u.getRole(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getBirthDate(), u.getGenre() , teacherDisciplinesSummary, studentCategoriesDto);

            userDTOList.add(expandedUserDTO);
        }

        return userDTOList;    }

    @Override
    @Transactional
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

        //todo crear cuotas
        currentUser = feeService.CreateFeesForStudent(currentUser);
        userRepo.save(currentUser);

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

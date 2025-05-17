package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public UserDTO updateKeycloakUser(UserDTO userDTO){
        return null;
    }

    @Override
    public boolean deleteKeycloakUserByKeycloakId(String userKeycloakId){
        return false;
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
        String genreStr = jwt.getClaimAsString("genre");

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

    //todo verificar q sea Role STUDENT
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

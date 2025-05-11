package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
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
    public ExpandedUserDTO syncUserFromKeycloak(Jwt jwt) {
        //datos del User q devuelve keycloak al hacer login
        String keycloakId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        //LocalDate birthDate
        //Genre genre
        Role role = this.getRoleFromJwt(jwt);

        User currentUser = userRepo.findByKeycloakId(keycloakId)
                //update si ya existe
                .map(existingUser -> {
                    existingUser.setEmail(email);
                    existingUser.setKeycloakId(keycloakId);
                    existingUser.setUsername(username);
                    existingUser.setFirstName(firstName != null ? firstName : username);
                    existingUser.setLastName(lastName != null ? lastName : "-");
                    //birthDate
                    //genre

                    //actualiza y devuelve el mismo User pero con los datos de mi DB
                    return userRepo.save(existingUser);
                })
                // lo crea si NO existe
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setKeycloakId(keycloakId);
                    newUser.setUsername(username);
                    newUser.setFirstName(firstName != null ? firstName : username);
                    newUser.setLastName(lastName != null ? lastName : "-");
                    //birthDate
                    //genre

                    //crea y devuelve el mismo User pero con los datos de mi DB
                    return userRepo.save(newUser);
                });

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

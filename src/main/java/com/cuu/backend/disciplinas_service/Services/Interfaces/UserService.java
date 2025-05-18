package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UserService {
    ExpandedUserDTO syncUserFromKeycloak(Jwt jwt);

    ExpandedUserDTO updateKeycloakUser(String keycloakId, UserDTO userDTO);

    boolean deleteKeycloakUserByKeycloakId(String userKeycloakId);

    List<ExpandedUserDTO> getAllUsers();

}

package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import com.cuu.backend.disciplinas_service.Services.RestClients.KeycloakAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<List<ExpandedUserDTO>> getAllUsers() {
        List<ExpandedUserDTO> resp = userService.getAllUsers();

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/get-all/by-role/{role}")
    public ResponseEntity<List<ExpandedUserDTO>> getAllUsersByRole(@PathVariable Role role) {
        List<ExpandedUserDTO> resp = userService.getAllUsersByRole(role);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/get-current-user-info")
    public ExpandedUserDTO getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.syncUserFromKeycloak(jwt);
    }

    @DeleteMapping("/delete/{keycloakId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String keycloakId) {
        Boolean resp = userService.deleteKeycloakUserByKeycloakId(keycloakId);

        return ResponseEntity.ok(resp);
    }

    @PutMapping("/update/{keycloakId}")
    public ResponseEntity<ExpandedUserDTO> updateUser(@PathVariable String keycloakId, @RequestBody ExpandedUserDTO expandedUserDTO) {
        ExpandedUserDTO resp = userService.updateKeycloakUser(keycloakId, expandedUserDTO);

        return ResponseEntity.ok(resp);
    }

}

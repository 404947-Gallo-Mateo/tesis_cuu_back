package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend.ExpandedUserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Services.Interfaces.UserService;
import com.cuu.backend.disciplinas_service.Services.RestClients.KeycloakAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KeycloakAdminClient keycloakAdminClient;

    @GetMapping("/get-current-user-info")
    public ExpandedUserDTO getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.syncUserFromKeycloak(jwt);
    }

    @DeleteMapping("/users/{keycloakId}")
    public ResponseEntity<?> deleteUser(@PathVariable String keycloakId) {
        String token = keycloakAdminClient.getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        restTemplate.exchange(
                "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + keycloakId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        // luego eliminás al usuario también de tu DB
        userService.deleteKeycloakUserByKeycloakId(keycloakId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{keycloakId}")
    public ResponseEntity<?> updateUser(@PathVariable String keycloakId, @RequestBody UserDTO userDto) {
        String token = keycloakAdminClient.getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // armás la estructura que espera Keycloak
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", userDto.getFirstName());
        body.put("lastName", userDto.getLastName());
        body.put("email", userDto.getEmail());
        body.put("enabled", true);

        restTemplate.exchange(
                "http://localhost:8080/admin/realms/Club_Union_Unquillo/users/" + keycloakId,
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                Void.class
        );

        // actualizar en tu DB también
        userService.updateKeycloakUser(userDto);

        return ResponseEntity.ok().build();
    }


}

package com.cuu.backend.disciplinas_service.Services.RestClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakAdminClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String KEYCLOAK_URL = "http://localhost:8080";
    private final String REALM = "Club_Union_Unquillo";
    private final String CLIENT_ID = "cuu-back-admin-cli";
    private final String CLIENT_SECRET = "ngUM24NdY9miAIiG4NFj2xd9Xei5hykB";

    public String getAdminToken() {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);

        var response = restTemplate.postForEntity(
                KEYCLOAK_URL + "/realms/" + REALM + "/protocol/openid-connect/token",
                new HttpEntity<>(params, new HttpHeaders()),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}


package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.MPFeeDTO;
import com.cuu.backend.disciplinas_service.Services.Interfaces.MercadoPagoService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/mercado-pago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping
    public ResponseEntity<Preference> createPreference(@RequestBody MPFeeDTO mpFeeDTO) throws MPException, MPApiException {
        return ResponseEntity.ok(mercadoPagoService.createPreference(mpFeeDTO));
    }

    @PostMapping("/{feeId}")
    public ResponseEntity<String> receiveNotification(@RequestBody Map<String, Object> body, @PathVariable(name = "feeId") UUID feeId) throws MPException, MPApiException {
        String topic = (String) body.get("topic");
        String resource = (String) body.get("resource");
        return ResponseEntity.ok(mercadoPagoService.receiveNotification(topic, resource, feeId));
    }
}

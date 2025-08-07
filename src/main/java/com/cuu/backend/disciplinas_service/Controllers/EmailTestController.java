package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Services.Interfaces.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/smtp-test")
public class EmailTestController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam String to) {
        emailService.sendSimpleEmail(to, "Prueba de Spring Boot", "¡Hola! Esto es un correo de prueba enviado desde Spring Boot con Gmail.");
        return "Correo enviado a " + to;
    }
}

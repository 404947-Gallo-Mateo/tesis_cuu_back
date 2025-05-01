package com.cuu.backend.disciplinas_service.Controllers;

import com.cuu.backend.disciplinas_service.Services.Interfaces.StudentInscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/student_inscription")
public class StudentInscriptionController {

    @Autowired
    private StudentInscriptionService studentInscription;
}

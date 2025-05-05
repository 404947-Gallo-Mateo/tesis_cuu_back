package com.cuu.backend.disciplinas_service.Services.Validators;

import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryValidatorImpl {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;
}

package com.szakikereso.backend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.service.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalController {
    private final ProfessionalService professionalService;


    @GetMapping
    public List<Professional> getAll() {
        return professionalService.findAll();
    }

    @GetMapping("/search")
    public List<Professional> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "false") boolean urgent
    ) {
        return professionalService.search(name, city, specialty, date, urgent);
    }

    @PostMapping
    public Professional create(@RequestBody Professional p) {
        return professionalService.save(p);
    }


}

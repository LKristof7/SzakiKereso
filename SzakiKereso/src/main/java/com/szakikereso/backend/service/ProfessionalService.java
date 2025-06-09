package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.repository.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalService {
    private final ProfessionalRepository professionalRepository;

    public List<Professional> findAll() {
        return professionalRepository.findAll();
    }

    public List<Professional> search(
            String name,
            String city,
            String specialty,
            LocalDateTime slot,
            boolean urgent
    )  {
        return professionalRepository.advancedSearch(name, city, specialty, slot, urgent);
    }

    public Professional save(Professional p) {
        return professionalRepository.save(p);
    }

    public List<String> suggestSpecialties(String prefix){
        return professionalRepository.findDistinctSpecialtiesStartingWith(prefix.toLowerCase());
    }

    public List<String> suggestNames(String prefix) {
        return professionalRepository.findDistinctNameStartingWith(prefix.toLowerCase());
    }

    public List<String> suggestCity(String prefix) {
        return professionalRepository.findDistinctCityStartingWith(prefix.toLowerCase());
    }

    @Transactional
    public Professional getProfessionalWithSlots(Long id){
        Professional p = professionalRepository.findById(id).orElseThrow(() -> new RuntimeException("Nincs ilyen szakember:"+id));

        p.getTimeSlots().size();
        return p;
    }
}

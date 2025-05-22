package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.repository.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalService {
    private final ProfessionalRepository repo;

    public List<Professional> findAll() {
        return repo.findAll();
    }

    public List<Professional> search(
            String name,
            String city,
            String specialty,
            LocalDateTime slot,
            boolean urgent
    ) {
        if (name != null && !name.isBlank()) {
            return repo.findByNameContainingIgnoreCase(name);
        }
        if (city != null && specialty != null) {
            return repo.findByCityIgnoreCaseAndSpecialtyContainingIgnoreCase(city, specialty);
        }
        if (slot != null) {
            return repo.findByAvailableSlotsContains(slot);
        }
        return repo.findAll();
    }

    public Professional save(Professional p) {
        return repo.save(p);
    }

    public Professional bookSlot(Long profId, LocalDateTime slot) {
        var p = repo.findById(profId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen szakember"));
        if (!p.getAvailableSlots().remove(slot)) {
            throw new IllegalStateException("Az időpont már foglalt");
        }
        return repo.save(p);
    }
}

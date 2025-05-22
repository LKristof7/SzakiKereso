package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    // Név szerinti keresés (részszöveg)
    List<Professional> findByNameContainingIgnoreCase(String name);

    // Város szerint
    List<Professional> findByCityIgnoreCase(String city);

    // Szakterület (pontosan vagy részben)
    List<Professional> findBySpecialtyContainingIgnoreCase(String specialty);

    // Akár több feltétel kombinálva is lehet
    List<Professional> findByCityIgnoreCaseAndSpecialtyContainingIgnoreCase(String city, String specialty);

    // Szabad időpont szerinti keresés (ha a slot benne van a készletben)
    List<Professional> findByAvailableSlotsContains(LocalDateTime slot);
}

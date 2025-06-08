package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByProfessionalId(Long professionalId);

    List<Booking> findByProfessionalIdAndStartTimeBetween(Long professionalId, LocalDateTime start, LocalDateTime end);
}

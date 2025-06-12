package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByProfessionalAndIsBookedFalseAndStartTimeAfterOrderByStartTimeAsc(Professional professional, LocalDateTime currentTime);
}

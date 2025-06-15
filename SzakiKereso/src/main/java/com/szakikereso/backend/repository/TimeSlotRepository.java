package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByProfessionalAndIsBookedFalseAndStartTimeAfterOrderByStartTimeAsc(Professional professional, LocalDateTime currentTime);

    @Query("SELECT MAX(ts.startTime) FROM TimeSlot ts")
    Optional<LocalDateTime> findLatestTimeSlot();

    @Modifying
    @Query("DELETE FROM TimeSlot ts WHERE ts.startTime < :cutoffDate")
    void deleteOldSlots(@Param("cutoffDate") LocalDateTime cutoffDate);

}

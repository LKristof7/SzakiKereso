package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByProfessionalId(Long professionalId);

    List<Booking> findByProfessionalIdAndStartTimeBetween(Long professionalId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE " +
            "LOWER(b.clientName) = LOWER(:clientName) AND " +
            "(LOWER(b.clientEmail) = LOWER(:clientEmail) OR b.clientPhone = :clientPhone)")
    List<Booking> findBookingsByClientDetails(
            @Param("clientName")String clientName,
            @Param("clientEmail")String clientEmail,
            @Param("clientPhone")String clientPhone
    );

}

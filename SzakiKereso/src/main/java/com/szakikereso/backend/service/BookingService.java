package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.repository.BookingRepository;
import com.szakikereso.backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ProfessionalRepository professionalRepository;

    public Booking book(Long profId, String name, String email, String phone, LocalDateTime start) {
        var professional=professionalRepository.findById(profId).orElseThrow(()->new IllegalArgumentException("Nincs ilyen szakember"));

        var end=start.plusMinutes(30);

        var conflits=bookingRepository.findByProfessionalIdAndStartTimeBetween(profId, start.minusMinutes(29), end);
        if(!conflits.isEmpty()){
            throw new IllegalStateException("Ez az időpont már foglalt!");
        }

        Booking booking= Booking.builder()
                .professional(professional)
                .startTime(start)
                .endTime(end)
                .clientName(name)
                .clientEmail(email)
                .clientPhone(phone)
                .build();
        return bookingRepository.save(booking);
    }
}

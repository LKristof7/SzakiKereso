package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.repository.BookingRepository;
import com.szakikereso.backend.repository.ProfessionalRepository;
import com.szakikereso.backend.repository.TimeSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    //private final ProfessionalRepository professionalRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Booking createBooking(Long timeSlotId, String clientName, String clientEmail, String clientPhone) {
        // 1. Megkeressük a TimeSlot-ot
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen azonosítójú időpont."));

        // 2. Ellenőrizzük, hogy szabad-e még
        if (slot.isBooked()) {
            throw new IllegalStateException("Ez az időpont már foglalt.");
        }

        // 3. Lefoglalttá tesszük és mentjük
        slot.setBooked(true);
        timeSlotRepository.save(slot);

        // 4. Létrehozzuk és elmentjük a Booking entitást
        Booking newBooking = Booking.builder()
                .professional(slot.getProfessional())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .clientName(clientName)
                .clientEmail(clientEmail)
                .clientPhone(clientPhone)
                .build(); // A createdAt automatikusan beállítódik

        return bookingRepository.save(newBooking);
    }
}

package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.repository.BookingRepository;
import com.szakikereso.backend.repository.TimeSlotRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Booking createBooking(Long timeSlotId, String clientName, String clientEmail, String clientPhone) {
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen azonosítójú időpont."));
        if (slot.isBooked()) {
            throw new IllegalStateException("Ez az időpont már foglalt.");
        }

        slot.setBooked(true);
        timeSlotRepository.save(slot);

        Booking newBooking = Booking.builder()
                .professional(slot.getProfessional())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .clientName(clientName)
                .clientEmail(clientEmail)
                .clientPhone(clientPhone)
                .build();

        return bookingRepository.save(newBooking);
    }

    @Transactional(readOnly = true)
    public List<Booking> findBookings(String name, String email, String phone) {
        if (name == null || name.isBlank() || (email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            return Collections.emptyList();
        }
        return bookingRepository.findBookingsByClientDetails(name, email, phone);
    }
}

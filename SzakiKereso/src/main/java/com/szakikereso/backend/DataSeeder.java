package com.szakikereso.backend;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.repository.BookingRepository;
import com.szakikereso.backend.repository.ProfessionalRepository;
import com.szakikereso.backend.repository.TimeSlotRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfessionalRepository professionalRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;

    public DataSeeder(ProfessionalRepository prepository, TimeSlotRepository ptimeSlotRepository, BookingRepository pbookingRepository) {
        this.professionalRepository = prepository;
        this.timeSlotRepository = ptimeSlotRepository;
        this.bookingRepository = pbookingRepository;

    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Régi időpontok törlése és újak betöltése -DataSeeder futtatása-");

        List<Professional> professionals = professionalRepository.findAll();

        LocalDateTime cutoffDate=LocalDate.now().minusDays(1).atStartOfDay();
        timeSlotRepository.deleteOldSlots(cutoffDate);

        Optional<LocalDateTime> latestSlot=timeSlotRepository.findLatestTimeSlot();

        LocalDate today=LocalDate.now();
        LocalDate generationStartDate;

        if (latestSlot.isPresent()) {
            // Ha már vannak időpontok, a generálást a legutolsó nap utáni naptól kezdjük.
            LocalDate lastGeneratedDay = latestSlot.get().toLocalDate();
            generationStartDate = lastGeneratedDay.plusDays(1);
        } else {
            // Ha a tábla üres, a mai naptól kezdünk.
            generationStartDate = today;
        }

        // Biztosítjuk, hogy soha ne generáljunk a múltba. Ha valamiért lemaradnánk,
        // a generálás akkor is a mai naptól induljon.
        if (generationStartDate.isBefore(today)) {
            generationStartDate = today;
        }

        LocalDate generationEndDate = today.plusDays(14); // Mindig 14 napra előre töltünk

        // 3. LÉPÉS: Új időpontok generálása, ha szükséges
        if (!generationStartDate.isAfter(generationEndDate)) {
            System.out.println("Új időpontok generálása " + generationStartDate + " és " + generationEndDate + " között.");

            if (professionals.isEmpty()) {
                System.out.println("Nincsenek szakemberek, nincs mihez időpontot generálni.");
                return;
            }

            List<TimeSlot> newSlots = new ArrayList<>();
            // A generálás logikája ugyanaz, mint eddig
            int startHour = 7;
            int endHour = 15;

            for (LocalDate date = generationStartDate; !date.isAfter(generationEndDate); date = date.plusDays(1)) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                if (dayOfWeek != DayOfWeek.SUNDAY) { // Vasárnap nem dolgozunk
                    for (Professional professional : professionals) {
                        for (int hour = startHour; hour < endHour; hour++) {
                            TimeSlot newSlot = new TimeSlot();
                            newSlot.setProfessional(professional);
                            newSlot.setStartTime(LocalDateTime.of(date, LocalTime.of(hour, 0)));
                            newSlot.setEndTime(LocalDateTime.of(date, LocalTime.of(hour + 1, 0)));
                            newSlot.setBooked(false);
                            newSlots.add(newSlot);
                        }
                    }
                }
            }
            timeSlotRepository.saveAll(newSlots);
            System.out.println(newSlots.size() + " darab új időpont generálva.");
            makeItRealistic(newSlots);
        } else {
            System.out.println("Nincs szükség új időpontok generálására, a rendszer naprakész.");
        }

    }

    private void makeItRealistic(List<TimeSlot> allGeneratedSlots) {
        if(allGeneratedSlots.isEmpty()) {
            return;
        }

        Collections.shuffle(allGeneratedSlots);

        //Kb 20%-ot lefoglalunk
        int numberofSlitsToBook=allGeneratedSlots.size()/5;
        List<TimeSlot> slotsToBook=allGeneratedSlots.subList(0,numberofSlitsToBook);
        List<Booking> newBookings=new ArrayList<>();

        for (TimeSlot slot : slotsToBook) {
            slot.setBooked(true);

            //Fiktív foglalások
            Booking booking = Booking.builder()
                    .professional(slot.getProfessional())
                    .startTime(slot.getStartTime())
                    .endTime(slot.getEndTime())
                    .clientName("Teszt Kliens " + (int)(Math.random() * 1000))
                    .clientEmail("tesztkliens" + (int)(Math.random() * 1000) + "@teszt.hu")
                    .clientPhone("06301234567")
                    .createdAt(LocalDateTime.now())
                    .build();
            newBookings.add(booking);
        }

        // A módosított (már foglalt) időpontok és az új foglalások mentése
        timeSlotRepository.saveAll(slotsToBook);
        bookingRepository.saveAll(newBookings);

        System.out.println(newBookings.size() + " darab fiktív foglalás mentve");
    }
}

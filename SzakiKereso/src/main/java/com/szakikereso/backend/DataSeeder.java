package com.szakikereso.backend;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.repository.BookingRepository;
import com.szakikereso.backend.repository.ProfessionalRepository;
import com.szakikereso.backend.repository.TimeSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public void run(String... args) throws Exception {
        if(timeSlotRepository.count() > 0) {
            System.out.println("Időpontok már léteznek az adatbázisban, a DataSeeder nem fut le");
            return;
        }

        System.out.println("DataSeeder futtatása: Időpontok generálása");

        List<Professional> professionals = professionalRepository.findAll();
        if(professionals.isEmpty()) {
            System.out.println("Nincsenek szakemberek a listában, a DataSeeder nem fut le");
            return;
        }

        List<TimeSlot> allGeneratedSlots = new ArrayList<>();
        //H-SZO 7-18 alapján generálunk
        int startHour=7;
        int endHour=18;

        for (Professional professional : professionals) {
            //Generálunk a következő 14 napra
            for (int i=0;i<14;i++){
                LocalDate currentDate = LocalDate.now().plusDays(i);
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                if(dayOfWeek!=DayOfWeek.SUNDAY){
                    for (int hour=startHour;hour<endHour;hour++){
                        TimeSlot newtimeSlot = new TimeSlot();
                        newtimeSlot.setProfessional(professional);
                        newtimeSlot.setStartTime(LocalDateTime.of(currentDate, LocalTime.of(hour, 0)));
                        newtimeSlot.setEndTime(LocalDateTime.of(currentDate, LocalTime.of(hour + 1, 0)));
                        newtimeSlot.setBooked(false);
                        allGeneratedSlots.add(newtimeSlot);

                    }
                }
            }
        }

        timeSlotRepository.saveAll(allGeneratedSlots);
        System.out.println(allGeneratedSlots.size()+" darab szabad időpont sikeresen generálva és mentve");

        //Random néha időpont "foglalt" lesz
        makeItRealistic(allGeneratedSlots);
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

package com.szakikereso.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String specialty;
    private String phone;
    private String email;
    private String city;
    private double pricePerHour;
    private boolean urgentAvailable;

    // Elérhető időpontok
    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots=new ArrayList<>();

    // Vélemények
    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;
}

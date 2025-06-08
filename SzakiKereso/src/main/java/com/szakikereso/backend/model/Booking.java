package com.szakikereso.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professional_id")
    private Professional professional;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private LocalDateTime createdAt=LocalDateTime.now();
}

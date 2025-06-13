package com.szakikereso.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;       // 0–5 csillag
    private String comment;   // szöveges vélemény

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professional professional;
}

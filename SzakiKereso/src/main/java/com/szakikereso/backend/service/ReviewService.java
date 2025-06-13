package com.szakikereso.backend.service;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.Review;
import com.szakikereso.backend.repository.ProfessionalRepository;
import com.szakikereso.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProfessionalRepository professionalRepository;

    @Transactional
    public Review addReview(Long professionalId, int rating, String comment) {
        Professional prof = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new IllegalArgumentException("Nem található szakember ezzel az azonosítóval: " + professionalId));

        Review newReview = new Review();
        newReview.setProfessional(prof);
        newReview.setRating(rating);
        newReview.setComment(comment);

        return reviewRepository.save(newReview);
    }
}

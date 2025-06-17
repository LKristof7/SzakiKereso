package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.Review;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import com.szakikereso.backend.service.ProfessionalService;
import com.szakikereso.frontend.util.DialogFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfessionalDetailController {


    @FXML private VBox detailsRootPane;
    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label cityLabel;
    @FXML private Label phoneLabel;
    @FXML private Label priceLabel;
    @FXML private Label emailLabel;
    @FXML private VBox reviewsContainer;


    private final ProfessionalService professionalService;
    private final BookingService bookingService;

    private Professional currentProfessional;

    public ProfessionalDetailController(ProfessionalService professionalService, BookingService bookingService) {
        this.professionalService = professionalService;
        this.bookingService = bookingService;
    }

    public void displayProfessional(Professional professional) {
        this.currentProfessional = professional;

        detailsRootPane.setVisible(true);

        nameLabel.setText(professional.getName());
        specialtyLabel.setText(professional.getSpecialty());
        cityLabel.setText(professional.getCity());
        phoneLabel.setText(professional.getPhone());
        priceLabel.setText(professional.getPricePerHour()+" Ft/óra");
        emailLabel.setText(professional.getEmail());

        reviewsContainer.getChildren().clear();
        if(professional.getReviews() == null || professional.getReviews().isEmpty()) {
            reviewsContainer.getChildren().add(new Label("Még nincsenek vélemények"));
        }
        else {
            for (Review review : professional.getReviews()) {
                VBox reviewCard= new VBox(5);
                reviewCard.getStyleClass().add("review-card");

                int rating = review.getRating();
                String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
                Label ratingLabel = new Label(stars);
                ratingLabel.getStyleClass().add("rating-stars");
                ratingLabel.setStyle("-fx-text-fill: #f1c40f;");

                Text commentText= new Text(review.getComment());
                commentText.setWrappingWidth(300);

                reviewCard.getChildren().addAll(ratingLabel,commentText);
                reviewsContainer.getChildren().add(reviewCard);
            }
        }
    }

    @FXML
    public void onBook() {
        if(currentProfessional == null) {
            return;
        }
        List<TimeSlot> availableSlots=professionalService.getAvailableTimeSlots(currentProfessional.getId());

        DialogFactory.showBookingDialog(currentProfessional,availableSlots,bookingService, ()->displayProfessional(currentProfessional));
        displayProfessional(currentProfessional);
    }

    @FXML
    public void initialize(){
        detailsRootPane.setVisible(false);
    }
}

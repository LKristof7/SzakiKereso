package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.Review;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalDetailController {

    @FXML private VBox detailsRootPane;
    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label cityLabel;
    @FXML private Label phoneLabel;
    @FXML private Label priceLabel;
    @FXML private Button bookingButton;
    @FXML private VBox reviewsContainer;

    private Professional currentProfessional;

    public void displayProfessional(Professional professional) {
        this.currentProfessional = professional;

        detailsRootPane.setVisible(true);

        nameLabel.setText(professional.getName());
        specialtyLabel.setText(professional.getSpecialty());
        cityLabel.setText(professional.getCity());
        phoneLabel.setText(professional.getPhone());
        priceLabel.setText(professional.getPricePerHour()+" Ft/óra");

        reviewsContainer.getChildren().clear();
        if(professional.getReviews() == null || professional.getReviews().isEmpty()) {
            reviewsContainer.getChildren().add(new Label("Még nincsenek vélemények"));
        }
        else {
            for (Review review : professional.getReviews()) {
                VBox reviewCard= new VBox(5);
                reviewCard.setStyle("-fx-border-color: #cccccc; -fx-padding: 10; -fx-background-color: white;");

                Text ratingText= new Text("Értékelés: "+review.getRating()+" /5");
                Text commentText= new Text(review.getComment());
                commentText.setWrappingWidth(300);

                reviewCard.getChildren().addAll(ratingText,commentText);
                reviewsContainer.getChildren().add(reviewCard);
            }
        }
    }

    @FXML
    public void onBook() {
        if(currentProfessional != null) {
            System.out.println("Foglalás indítása");
            //Itt
        }
    }

    @FXML
    public void initialize(){
        detailsRootPane.setVisible(false);
    }
}

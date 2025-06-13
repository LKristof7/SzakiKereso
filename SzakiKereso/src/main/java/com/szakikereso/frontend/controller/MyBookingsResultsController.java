package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.service.ReviewService;
import com.szakikereso.frontend.util.DialogFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.Button;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MyBookingsResultsController {

    @FXML private VBox bookingsContainer;
    @FXML private Button backButton;

    private final ReviewService reviewService;
    private Runnable backAction;

    @Autowired
    public MyBookingsResultsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void displayBookings(List<Booking> booking){
        bookingsContainer.getChildren().clear();

        if(booking == null || booking.isEmpty()){
            bookingsContainer.getChildren().add(new Label("Nem található foglalás"));
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for(Booking book : booking){
            VBox card=new VBox(10);
            card.getStyleClass().add("cardBooking");

            Label professionalLabel=new Label("Szakember: "+book.getProfessional().getName());
            professionalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            Label timeLabel = new Label("Időpont: " + book.getStartTime().format(formatter));

            Button reviewButton = new Button("Vélemény írása");
            reviewButton.setOnAction(event -> {
                DialogFactory.showReviewDialog(book.getProfessional(),reviewService);

                //reviewButton.setDisable(true);
            });

            card.getChildren().addAll(professionalLabel, timeLabel, reviewButton);
            bookingsContainer.getChildren().add(card);
        }
    }

    @FXML
    void onBack(ActionEvent event) {
        if(backAction != null){
            backAction.run();
        }
    }

    public void setBackAction(Runnable backAction){
        this.backAction = backAction;
    }
}

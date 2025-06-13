package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Booking;
import com.szakikereso.backend.service.BookingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class MyBookingsController {
    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private Button searchButton;

    private final BookingService bookingService;
    private final MainPageController mainPageController;

    @Autowired
    public MyBookingsController(BookingService bookingService, MainPageController mainPageController) {
        this.bookingService = bookingService;
        this.mainPageController= mainPageController;
    }

    @FXML
    void onSearchBookings(ActionEvent event) {
        String name=nameField.getText();
        String contact=contactField.getText();

        if (name.isBlank() || contact.isBlank()) {
            System.out.println("Kérjük, töltse ki mindkét mezőt!");
            return;
        }
        String email=null;
        String phone =null;
        if(contact.contains("@")){
            email=contact;
        }else{
            phone=contact;
        }

        List<Booking> myBookings=bookingService.findBookings(name,email,phone);
        mainPageController.showBookingResults(myBookings);

    }
}

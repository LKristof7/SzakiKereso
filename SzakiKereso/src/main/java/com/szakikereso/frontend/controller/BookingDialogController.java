package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.service.BookingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class BookingDialogController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> timeBox;

    @Autowired
    private BookingService bookingService;

    private Professional selectedProfessional;

    public void setProfessional(Professional p) {
        this.selectedProfessional = p;

        timeBox.getItems().clear();
        for (int h=8; h<16; h++){
            timeBox.getItems().add(LocalTime.of(h, 0));
        }
    }

    @FXML
    public void onBook() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime time = timeBox.getValue();

        if(name.isBlank() || email.isBlank() || phone.isBlank() || date==null || time==null) {
            showAlert("Minden mezőt ki kell tölteni!");
        }

        LocalDateTime start=LocalDateTime.of(date, time);

        try{
            bookingService.book(selectedProfessional.getId(), name,email,phone,start);
            showAlert("Foglalás sikeres!");
        }catch (Exception e) {
            showAlert("Hiba történt: "+e.getMessage());
        }
    }

    private void showAlert(String s) {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.showAndWait();
    }
}

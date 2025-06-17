package com.szakikereso.frontend.controller;

/*---------------------------------------------------------*/
/*---- OUTDATED -------------------- CAN BE DELETED ------*/
/*-------------------------------------------------------*/

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BookingDialogController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<TimeSlot> timeSlotBox;

    @Autowired
    private BookingService bookingService;

    private Professional selectedProfessional;

    public void setProfessional(Professional p) {
        this.selectedProfessional = p;

        timeSlotBox.getItems().clear();
        List<TimeSlot> freeSlots = p.getTimeSlots().stream().filter(slot -> !slot.isBooked()).toList();
        timeSlotBox.getItems().addAll(freeSlots);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        timeSlotBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimeSlot slot) {
                return slot == null ? "" : slot.getStartTime().format(formatter);
            }

            @Override
            public TimeSlot fromString(String string) {
                return null;
            }
        });

    }

    @FXML
    public void onBook() {
        TimeSlot selectedSlot= timeSlotBox.getValue();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if(name.isBlank() || email.isBlank() || phone.isBlank() || selectedSlot == null) {
            showAlert(Alert.AlertType.ERROR,"Minden mezőt ki kell tölteni!");
        }

        try{
            bookingService.createBooking(selectedProfessional.getId(), name,email,phone);
            showAlert(Alert.AlertType.INFORMATION,"Foglalás sikeres!");
            closeDialog();
        }catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Hiba történt: "+e.getMessage());
        }
    }

    private void closeDialog() {
        Stage stage=(Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

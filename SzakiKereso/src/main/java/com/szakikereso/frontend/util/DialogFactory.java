package com.szakikereso.frontend.util;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogFactory {

    public static void showBookingDialog(Professional professional, List<TimeSlot> availableSlots, BookingService bookingService) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "A szakembernek jelenleg nincs szabad időpontja.").showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Időpont foglalás: " + professional.getName());

        ComboBox<TimeSlot> slotBox = new ComboBox<>();
        slotBox.getItems().addAll(availableSlots);
        slotBox.setPromptText("Válassz időpontot");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        slotBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimeSlot slot) {
                return slot == null ? "" : slot.getStartTime().format(formatter);
            }
            @Override
            public TimeSlot fromString(String string) { return null; }
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Név");
        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Telefonszám");

        ButtonType bookButtonType = new ButtonType("Foglalás", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10, slotBox, nameField, emailField, phoneField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
                TimeSlot selected = slotBox.getValue();
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                if (selected != null && !name.isBlank() && !email.isBlank()) {
                    try {
                        bookingService.createBooking(selected.getId(), name, email, phone);
                        new Alert(Alert.AlertType.INFORMATION, "Sikeres foglalás!").showAndWait();
                    } catch (Exception e) {
                        new Alert(Alert.AlertType.ERROR, "Hiba: " + e.getMessage()).showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.WARNING, "Kérjük, töltsön ki minden mezőt!").showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}

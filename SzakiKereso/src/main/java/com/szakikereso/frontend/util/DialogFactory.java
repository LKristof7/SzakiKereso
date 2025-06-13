package com.szakikereso.frontend.util;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;


import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogFactory {

    public static void showBookingDialog(Professional professional, List<TimeSlot> availableSlots, BookingService bookingService, Runnable onBookingSuccessAction) {
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

        final Button bookBtn=(Button)dialog.getDialogPane().lookupButton(bookButtonType);

        bookBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();

            TimeSlot selected = slotBox.getValue();
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (selected == null || name.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Hiba: Minden mezőt ki kell tölteni!");
                return;
            }
            if(!isValidEmail(email)) {
                showAlert(Alert.AlertType.ERROR, "Hiba: Érvénytelen e-mail cím formátum!");
                return;
            }
            if(!isValidPhone(phone)) {
                showAlert(Alert.AlertType.ERROR, "Hiba: Érvénytelen telefonszám formátum! \nCsak számokat, szóközt, '+' és '-' jelet tartalmazhat.");
                return;
            }

            try {
                bookingService.createBooking(selected.getId(), name, email, phone);
                showAlert(Alert.AlertType.INFORMATION, "Sikeres foglalás!");
                if (onBookingSuccessAction != null) {
                    onBookingSuccessAction.run();
                }
                dialog.close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Hiba a foglalás során: " + e.getMessage());
            }

        });

        dialog.showAndWait();
    }

    private static boolean isValidEmail(String email){
        if(email ==null || email.isBlank()){
            return false;
        }
        String emailRegex="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private static boolean isValidPhone(String phone){
        if(phone ==null || phone.isBlank()){
            return false;
        }
        String phoneRegex="^[0-9+\\s-]+$";
        return phone.matches(phoneRegex);
    }

    private static void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

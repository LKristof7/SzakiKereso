package com.szakikereso.frontend.util;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import com.szakikereso.backend.service.ReviewService;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class DialogFactory {

    public static void showBookingDialog(Professional professional, List<TimeSlot> availableSlots, BookingService bookingService, Runnable onBookingSuccessAction) {
        if (availableSlots == null || availableSlots.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "A szakembernek jelenleg nincs szabad időpontja!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        applyStylesToDialog(dialog);
        dialog.getDialogPane().getStyleClass().add("booking-dialog");
        dialog.getDialogPane().setPrefSize(400,300);

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

        ButtonType saveButtonType = new ButtonType("Mentés", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType= new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);

        VBox content = new VBox(10, slotBox, nameField, emailField, phoneField);
        dialog.getDialogPane().setContent(content);

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
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
        applyStylesToDialog(alert);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showReviewDialog(Professional professional, ReviewService reviewService) {
        Dialog<Void> dialog=new Dialog<>();
        applyStylesToDialog(dialog);
        dialog.getDialogPane().getStyleClass().add("review-dialog");
        dialog.setTitle("Vélemény írása: " + professional.getName());
        dialog.setHeaderText("Kérjük értékelje a szakembert!");

        Slider ratingSlider = new Slider(1,5,3);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setSnapToTicks(true);

        ratingSlider.setOnMouseClicked((MouseEvent event) -> {
            double clickPosition = event.getX() / ratingSlider.getWidth();
            double range = ratingSlider.getMax() - ratingSlider.getMin();
            double calculatedValue = ratingSlider.getMin() + (clickPosition * range);
            long roundedValue = Math.round(calculatedValue);
            ratingSlider.setValue(roundedValue);
        });

        TextArea reviewArea = new TextArea();
        reviewArea.setEditable(true);
        reviewArea.setPromptText("Írja le a véleményét.....");

        ButtonType saveButtonType = new ButtonType("Mentés", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType= new ButtonType("Mégse", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);

        VBox content=new VBox(20,ratingSlider, reviewArea);
        dialog.getDialogPane().setContent(content);

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();

            int rating=(int)ratingSlider.getValue();
            String review=reviewArea.getText();

            if(review.isBlank()){
                showAlert(Alert.AlertType.WARNING, "Kérjük írjon szöveges véleményt!");
                return;
            }

            try{
                reviewService.addReview(professional.getId(), rating, review);
                showAlert(Alert.AlertType.INFORMATION, "Köszönjük a véleményét!");
                dialog.close();
            }catch (Exception e){
                showAlert(Alert.AlertType.ERROR, "Hiba a mentés során: " + e.getMessage());
            }
        });
        dialog.showAndWait();
    }

    private static void applyStylesToDialog(Dialog<?> dialog) {
        DialogPane dialogPane=dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(DialogFactory.class.getResource("/styles/main.css")).toExternalForm());
    }
}


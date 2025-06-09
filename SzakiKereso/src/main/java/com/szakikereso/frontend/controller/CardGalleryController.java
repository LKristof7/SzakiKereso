package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.service.ProfessionalService;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.text.Text;

import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class CardGalleryController {
    @FXML private TextField nameField;
    @FXML private TextField cityField;
    @FXML private TextField specialtyField;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox urgentBox;
    @FXML private GridPane cardGrid;
    @FXML private Label pageLabel;

    @Autowired
    private  ProfessionalService service;

    private List<Professional> results;
    private int currentPage = 0;
    private int pageSize = 8;
    @Autowired
    private ProfessionalService professionalService;


    public CardGalleryController() {
    }

    @FXML
    public void initialize() {
        setupAutoComplete(nameField, service::suggestNames);
        setupAutoComplete(cityField, service::suggestCity);
        setupAutoComplete(specialtyField, service::suggestSpecialties);

        //Frissít amikor gépel
        nameField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        cityField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        specialtyField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> performSearch());
        urgentBox.selectedProperty().addListener((obs, oldVal, newVal) -> performSearch());

        performSearch();
    }

    @FXML
    private void onSearch() {
        performSearch();
    }

    private void performSearch() {
        String name=emptyToNull(nameField.getText());
        String city=emptyToNull(cityField.getText());
        String specialty= emptyToNull(specialtyField.getText());
        LocalDate date=datePicker.getValue();
        boolean urgent=urgentBox.isSelected();

        LocalDateTime slot=(date!=null)? date.atStartOfDay(): null;

        results=service.search(name,city,specialty,slot,urgent);
        currentPage=0;
        updateGrid();

    }

    private void updateGrid() {
        cardGrid.getChildren().clear();
        int from=currentPage*pageSize;
        int to=Math.min(from+pageSize,results.size());
        List<Professional> page=results.subList(from,to);

        int col=0,row=0;
        for(Professional p:page)
        {
            VBox card=createCard(p);
            cardGrid.add(card,col,row);
            col++;
            if(col>=4){
                col=0;row++;
            }
        }
        int totalPages= (results.size()+pageSize-1)/pageSize;
        pageLabel.setText((currentPage+1)+" / "+totalPages);

    }

    private VBox createCard(Professional p){
        VBox vbox=new VBox(5);
        vbox.getStyleClass().add("card");
        Text nameText = new Text(p.getName());
        Text specText = new Text(p.getSpecialty());
        Text cityText = new Text(p.getCity());
        Text priceText = new Text(p.getPricePerHour() + " Ft/óra");

        Button book = new Button("Foglalás");
        book.getStyleClass().add("book-button");
        book.setOnAction(e -> {
            Professional fullyLoaded = professionalService.getProfessionalWithSlots(p.getId());
            System.out.println("Available slots: "+ fullyLoaded.getAvailableSlots());
            openBookingDialog(fullyLoaded);
        });

        vbox.getChildren().addAll(nameText, specText, cityText, priceText, book);
        return vbox;
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 0) { currentPage--; updateGrid(); }
    }

    @FXML
    private void onNextPage() {
        if ((currentPage+1)*pageSize < results.size()) { currentPage++; updateGrid(); }
    }

    private void openBookingDialog(Professional p) {
        Dialog<Void> dialog=new Dialog<>();
        dialog.setTitle("Időpont foglalás:"+p.getName());

        ComboBox<LocalDateTime> slotBox=new ComboBox<>();
        slotBox.getItems().addAll(p.getAvailableSlots());
        slotBox.setPromptText("Válassz időpontot");

        TextField nameField=new TextField();
        nameField.setPromptText("Név");

        TextField emailField=new TextField();
        emailField.setPromptText("E-mail");

        TextField phoneField =new TextField();
        phoneField.setPromptText("Telefonszám");

        ButtonType bookButtonType = new ButtonType("Foglalás", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10, slotBox, nameField, emailField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
                LocalDateTime selected = slotBox.getValue();
                String name = nameField.getText();
                String email = emailField.getText();
                if (selected != null && !name.isBlank() && !email.isBlank()) {
                    try {
                        service.bookSlot(p.getId(), selected); // backend hívás

                        Alert success = new Alert(Alert.AlertType.INFORMATION, "Sikeres foglalás!");
                        success.showAndWait();
                    } catch (Exception e) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Hiba: " + e.getMessage());
                        error.showAndWait();
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void setupAutoComplete(TextField field, Function<String, List<String>> suggester) {
        ContextMenu suggestionsPopup = new ContextMenu();

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<String> suggestions = suggester.apply(newText);
            if (suggestions.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<MenuItem> menuItems = new ArrayList<>();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);
                item.setOnAction(e -> {
                    field.setText(suggestion);
                    suggestionsPopup.hide();
                });
                menuItems.add(item);
            }

            suggestionsPopup.getItems().setAll(menuItems);
            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(field, Side.BOTTOM, 0, 0);
            }
        });
    }

    private String emptyToNull(String text) {
        return (text == null || text.isBlank()) ? null : text.trim();
    }


}

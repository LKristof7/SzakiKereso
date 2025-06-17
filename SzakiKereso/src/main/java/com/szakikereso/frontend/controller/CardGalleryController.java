package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.model.TimeSlot;
import com.szakikereso.backend.service.BookingService;
import com.szakikereso.backend.service.ProfessionalService;
import com.szakikereso.frontend.util.DialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;
import java.time.LocalDate;
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
    @FXML private AnchorPane detailViewPane;

    private ProfessionalService professionalService;
    private final ApplicationContext applicationContext;
    private BookingService bookingService;

    private ProfessionalDetailController detailController;

    private List<Professional> results;
    private int currentPage = 0;
    private int pageSize = 16;

    @Autowired
    public CardGalleryController(ProfessionalService professionalService, BookingService bookingService, ApplicationContext applicationContext) {
        this.professionalService = professionalService;
        this.bookingService = bookingService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        loadDetailsView();

        setupAutoComplete(nameField, professionalService::suggestNames);
        setupAutoComplete(cityField, professionalService::suggestCity);
        setupAutoComplete(specialtyField, professionalService::suggestSpecialties);

        //Listenerek
        nameField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        cityField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        specialtyField.textProperty().addListener((obs, oldVal, newVal) -> performSearch());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> performSearch());
        urgentBox.selectedProperty().addListener((obs, oldVal, newVal) -> performSearch());

        performSearch();
    }
    private void loadDetailsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/professional_details.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent detailsNode = loader.load();
            this.detailController = loader.getController();
            detailViewPane.getChildren().setAll(detailsNode);

            //kitölti a rendelkezésre álló helyet
            AnchorPane.setTopAnchor(detailsNode, 0.0);
            AnchorPane.setBottomAnchor(detailsNode, 0.0);
            AnchorPane.setLeftAnchor(detailsNode, 0.0);
            AnchorPane.setRightAnchor(detailsNode, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        results=professionalService.search(name,city,specialty,date,urgent);
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
            GridPane.setFillHeight(card, true);
            GridPane.setFillWidth(card, true);
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
        vbox.getStyleClass().add("cardProf");

        vbox.setPrefHeight(165);
        vbox.setMinHeight(165);
        vbox.setPrefWidth(155);
        vbox.setMaxWidth(155);

        vbox.setOnMouseClicked(mouseEvent -> {
            if(detailController!=null){
                Professional fullylLoaded= professionalService.getProfessionalWithDetails(p.getId());
                detailController.displayProfessional(fullylLoaded);
            }
        });

        Text nameText = new Text(p.getName());
        nameText.setWrappingWidth(140);
        VBox nameContainer = new VBox(nameText);
        nameContainer.getStyleClass().add("card-name-container");

        Text specText = new Text(p.getSpecialty());
        specText.setWrappingWidth(140);
        Text cityText = new Text(p.getCity());
        Text priceText = new Text(p.getPricePerHour() + " Ft/óra");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button book = new Button("Foglalás");
        book.getStyleClass().add("book-button");
        book.setOnAction(e -> {
            List<TimeSlot> availableSlots=professionalService.getAvailableTimeSlots(p.getId());
            DialogFactory.showBookingDialog(p,availableSlots,bookingService, ()->performSearch());
        });

        vbox.getChildren().addAll(nameContainer, specText, cityText, priceText, book);
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

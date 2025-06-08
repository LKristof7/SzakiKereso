package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Professional;
import com.szakikereso.backend.service.ProfessionalService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.text.Text;

import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class CardGalleryController {
    @FXML private TextField nameField;
    @FXML private TextField cityField;
    @FXML private ComboBox<String> specialtyBox;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox urgentBox;
    @FXML private GridPane cardGrid;
    @FXML private Label pageLabel;

    @Autowired
    private  ProfessionalService service;

    private List<Professional> results;
    private int currentPage = 0;
    private int pageSize = 8;

   /* @Autowired
    public CardGalleryController(ProfessionalService service) {
        this.service = service;
    }*/

    public CardGalleryController() {
    }

    @FXML
    public void initialize() {
        specialtyBox.getItems().addAll("Villanyszerelő","Vízszerelő","Asztalos","Lakatos","..");
        performSearch();
    }

    @FXML
    private void onSearch() {
        performSearch();
    }

    private void performSearch() {
        String name=nameField.getText();
        String city=cityField.getText();
        String specialty=specialtyBox.getValue();
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
        book.setOnAction(e -> openBookingDialog(p));

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
        // Új ablak vagy dialog a foglaláshoz
    }
}

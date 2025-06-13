package com.szakikereso.frontend.controller;

import com.szakikereso.backend.model.Booking;
import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MainPageController {
    private final MyBookingsResultsController myBookingsResultsController;
    @FXML private AnchorPane rootPane;
    @FXML private StackPane mainContent;

    private final ApplicationContext applicationContext;

    public MainPageController(ApplicationContext applicationContext, MyBookingsResultsController myBookingsResultsController) {
        this.applicationContext = applicationContext;
        this.myBookingsResultsController = myBookingsResultsController;
    }

    @FXML
    public void initialize() {
        showWelcomePage();
    }

    @FXML
    private void onShowWelcomePage() {
        showWelcomePage();
    }

    private void showWelcomePage() {
        FXMLLoader loader = loadView("/fxml/welcome_view.fxml");
        if (loader != null) {
            WelcomeController welcomeController = loader.getController();
            welcomeController.setBrowseAction(this::showGallery);

            mainContent.getChildren().clear();
            mainContent.getChildren().add(loader.getRoot());
        }
    }

    @FXML
    private void showGallery() {
        FXMLLoader loader = loadView("/fxml/card-gallery.fxml");
        if (loader != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(loader.getRoot());
        }
    }

    @FXML
    private void onShowMyBookings(){
        FXMLLoader loader = loadView("/fxml/my_bookings.fxml");
        if (loader != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(loader.getRoot());
        }
    }

    private FXMLLoader loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            loader.load();
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showBookingResults(List<Booking> bookings){
        FXMLLoader loader = loadView("/fxml/my_bookings_results.fxml");
        if (loader != null) {
            MyBookingsResultsController resultsController = loader.getController();
            resultsController.setBackAction(this::onShowMyBookings);
            resultsController.displayBookings(bookings);
            mainContent.getChildren().clear();
            mainContent.getChildren().add(loader.getRoot());
        }
    }
}

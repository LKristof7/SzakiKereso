package com.szakikereso.frontend.controller;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainPageController {
    @FXML private AnchorPane rootPane;
    @FXML private Pane mainContent;

    private final ApplicationContext applicationContext;

    public MainPageController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
}

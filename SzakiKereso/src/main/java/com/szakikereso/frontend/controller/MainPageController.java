package com.szakikereso.frontend.controller;

import com.szakikereso.SpringContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainPageController {
    @FXML private AnchorPane rootPane;
    @FXML private Pane mainContent;

    @FXML
    private void showGallery() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/card-gallery.fxml")
            );
            loader.setControllerFactory(clazz -> SpringContext.getContext().getBean(clazz));

            Pane gallery = loader.load();

            mainContent.getChildren().add(gallery);
        }catch (IOException e){
            e.printStackTrace();

        }
    }
}

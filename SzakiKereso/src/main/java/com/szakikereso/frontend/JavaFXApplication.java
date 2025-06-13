package com.szakikereso.frontend;

import com.szakikereso.SpringContext_CanBeDeletet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.szakikereso.SzakiKeresoApplication;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        super.init();
        springContext = SpringApplication.run(SzakiKeresoApplication.class);
       // SpringContext_CanBeDeletet.setContext(springContext);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(clazz -> springContext.getBean(clazz));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Szakember-kereső");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.example.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(LibraryApplication.class)
                .web(org.springframework.boot.WebApplicationType.SERVLET)
                .run();
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void stop() {
        context.close();
    }
}

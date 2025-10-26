package com.example.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        stage.setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN");
        stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        stage.setScene(scene);
        stage.show();
    }
}

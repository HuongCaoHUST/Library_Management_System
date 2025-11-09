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
        context = new SpringApplicationBuilder(LibraryApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        loader.setControllerFactory(context::getBean);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")));
        stage.setTitle("Đăng nhập - Hệ thống thư viện");
        System.out.println("EMAIL_USERNAME env: " + System.getenv("EMAIL_USERNAME"));
        System.out.println("EMAIL_PASSWORD env: " + System.getenv("EMAIL_PASSWORD"));
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}

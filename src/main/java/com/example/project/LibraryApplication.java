package com.example.project;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)

@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        Application.launch(JavaFxApp.class, args);
    }
}

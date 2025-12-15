package com.example.project.javafxcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class HomeController {

    @FXML
    private AnchorPane menuBar;

    public void loadMenuByRole(String role) {
        try {
            FXMLLoader loader = new FXMLLoader();

            if ("ADMIN".equals(role)) {
                loader.setLocation(getClass().getResource("/com/example/project/menu_bar.fxml"));
            } else if ("READER".equals(role)) {
                loader.setLocation(getClass().getResource("/com/example/project/reader_menu_bar.fxml"));
            }

            Parent menu = loader.load();

            menuBar.getChildren().setAll(menu);

            AnchorPane.setTopAnchor(menu, 0.0);
            AnchorPane.setLeftAnchor(menu, 0.0);
            AnchorPane.setRightAnchor(menu, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

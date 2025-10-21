package com.example.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LoadForm {
    protected Label notificationBadge;
    protected void loadForm(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof LoadForm) {
                ((LoadForm) controller).notificationBadge = this.notificationBadge;
                ((LoadForm) controller).updateNotificationBadge();
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNotificationBadge() {
        if (notificationBadge == null) return;

        Path filePath = Paths.get("./data/register_queue.txt");
        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                int count = lines.size();

                notificationBadge.setVisible(count > 0);
                if (count > 0) notificationBadge.setText(String.valueOf(count));
            } else {
                notificationBadge.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            notificationBadge.setVisible(false);
        }
    }

}

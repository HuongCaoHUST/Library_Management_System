package com.example.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Account_Login {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private void handleSignin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ Username và Password!");
            return;
        }

        boolean isLibrarian = username.matches("(?i)^HUB\\d{5}$");
        boolean isReader = username.matches("^\\d{8}$");

        Path filePath;

        if (isLibrarian) {
            filePath = Paths.get("data/librarians.csv");
        } else if (isReader) {
            filePath = Paths.get("data/readers.csv");
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Tên đăng nhập không hợp lệ!\n");
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean isAuthenticated = false;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",", -1);

                if (isLibrarian) {
                    if (fields.length <= 14) continue;

                    String fileUsername = fields[13].trim();
                    String filePassword = fields[14].trim();
                    String accountStatus = (fields.length > 15) ? fields[15].trim() : "Active";
                    String fullName = fields[1].trim();

                    if (username.equalsIgnoreCase(fileUsername) && password.equals(filePassword)){
                        isAuthenticated = true;
                        if (!"Active".equalsIgnoreCase(accountStatus)) {
                            showAlert(Alert.AlertType.WARNING, "Tài khoản của bạn hiện không hoạt động!");
                            return;
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công!\nXin chào " + fullName + "!");
                        loadForm(event, "home_form");
                        return;
                    }

                } else if (isReader) {
                    if (fields.length <= 18) continue;

                    String fileUsername = fields[17].trim();
                    String filePassword = fields[18].trim();
                    String accountStatus = (fields.length > 19) ? fields[19].trim() : "Active";
                    String fullName = fields[1].trim();

                    if (username.equalsIgnoreCase(fileUsername) && password.equals(filePassword)){
                        isAuthenticated = true;
                        if (!"Active".equalsIgnoreCase(accountStatus)) {
                            showAlert(Alert.AlertType.WARNING, "Tài khoản của bạn hiện không hoạt động!");
                            return;
                        }

                        showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công!\nXin chào " + fullName + "!");
                        // TODO: chuyển sang giao diện bạn đọc
                        return;
                    }
                }
            }

            if (!isAuthenticated) {
                showAlert(Alert.AlertType.ERROR, "Sai tên đăng nhập hoặc mật khẩu!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể đọc file dữ liệu!");
        }
    }



    private void loadForm(ActionEvent event, String form_name) {
        try {
            String fxmlFile = null;
            if (form_name == "home_form") {
                fxmlFile = "/com/example/project/home_form.fxml";
            } else if (form_name == "home_form_for_students") {
                fxmlFile = "/com/example/project/home_form_for_students.fxml";

            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());


            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Trang chủ");
            stage.centerOnScreen();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) throws IOException {
        loadScene(event, "/com/example/project/register_form.fxml");
    }
    private void loadScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void showAlert(Alert.AlertType type, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        alert.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }
}
package com.example.project;

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

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private void handleSignin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String role = checkAccount(username, password);

        if (role != null) {
            if (role.equals("PENDING")) {
                showAlert(Alert.AlertType.INFORMATION, "Tài khoản của bạn đang chờ duyệt!");
            } else if (role.equalsIgnoreCase("chuyenvien")) {
                showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công!");
                loadForm(event, "home_form.fxml", username);
            } else if (role.equalsIgnoreCase("ban_doc")) {
                showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công!");
                loadForm(event, "home_form_for_students.fxml", username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Tài khoản không hợp lệ!");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Sai tên đăng nhập hoặc mật khẩu!");
        }
    }
    private String checkAccount(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("./data/account.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String fileUser = parts[0].trim();
                    String filePass = parts[1].trim();
                    String role = parts[2].trim();
                    String status = parts[3].trim();

                    if (username.equals(fileUser) && password.equals(filePass)) {
                        if (status.equalsIgnoreCase("ready")) {
                            return role;
                        } else if (status.equalsIgnoreCase("pending")) {
                            return "PENDING";
                        }
                    }
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Không thể đọc file account.txt!");
        }
        return null;
    }

    private void loadForm(ActionEvent event, String fxmlFile, String mssv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            HomeController controller = loader.getController();
            controller.loadUserByMSSV(mssv);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Trang chủ");
            stage.centerOnScreen();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể mở giao diện: " + fxmlFile);
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) throws IOException {
        loadScene(event, "register_form.fxml");
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
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

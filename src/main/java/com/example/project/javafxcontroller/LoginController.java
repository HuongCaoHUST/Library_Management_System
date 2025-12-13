package com.example.project.javafxcontroller;

import com.example.project.apiservice.AuthApiService;
import com.example.project.dto.LoginResponse;
import com.example.project.security.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblErrors;

    @FXML
    private Button btnSignin;

    @FXML
    private Button btnSignup;

    private final FXMLLoader fxmlLoader = new FXMLLoader();

    private final AuthApiService loginApi = new AuthApiService();

    @FXML
    public void initialize() {
        btnSignin.setOnAction(event -> handleLogin());
        btnSignup.setOnAction(event -> onSignup());
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblErrors.setText("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        AuthApiService authApi = new AuthApiService();
        LoginResponse response = authApi.login(username, password);
        if (response.isSuccess()) {
            lblErrors.setText("Đăng nhập thành công!");


            UserSession session = UserSession.getInstance();

            session.setToken(response.getToken());
            session.setRole(response.getRole());
            session.setPermissions(response.getPermissions());

            switch (response.getRole()) {
                case "ADMIN":
                    openMainForm("/com/example/project/home_form.fxml");
                    break;

                case "LIBRARIAN":
                    openMainForm("/com/example/project/home_form.fxml");
                    break;

                case "READER":
                    openMainForm("/com/example/project/reader_home_form.fxml");
                    break;
            }
        } else  {
            lblErrors.setText("Tên đăng nhập hoặc mật khẩu không chính xác");
        }
    }

    private void openMainForm(String fxml_path) {
        try {
            Parent root = fxmlLoader.load(getClass().getResource(fxml_path));
            Scene homeScene = new Scene(root);
            Stage stage = (Stage) btnSignin.getScene().getWindow();
            stage.setScene(homeScene);
            stage.centerOnScreen();
            stage.setTitle("Trang chủ - Hệ thống quản lý thư viện");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblErrors.setText("Lỗi khi mở trang chủ: " + e.getMessage());
        }
    }

    private void onSignup() {
        try {
            Parent root = fxmlLoader.load(getClass().getResource("/com/example/project/signup_form.fxml"));
            Scene signupScene = new Scene(root);

            Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Đăng ký - Hệ thống quản lý thư viện");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblErrors.setText("Không thể mở form đăng ký: " + e.getMessage());
        }
    }
}

package com.example.project.controller;

import com.example.project.model.Librarian;
import com.example.project.repository.LibrarianRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
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

    @Autowired
    private LibrarianRepository librarianRepository;

    @FXML
    public void initialize() {
        btnSignin.setOnAction(event -> handleLogin());
        btnSignup.setOnAction(event -> openSignupForm());
    }

    private void handleLogin() {
        String usernameOrEmail = txtUsername.getText();
        String password = txtPassword.getText();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            lblErrors.setText("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        Optional<Librarian> librarianOpt = librarianRepository.findByUsernameAndPassword(usernameOrEmail, password);
        if (librarianOpt.isEmpty()) {
            librarianOpt = librarianRepository.findByEmailAndPassword(usernameOrEmail, password);
        }

        if (librarianOpt.isPresent()) {
            lblErrors.setText("");
            System.out.println("Đăng nhập thành công: " + librarianOpt.get().getFullName());
            openHomePage();
        } else {
            lblErrors.setText("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    private void openHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/home_form.fxml"));
            Scene homeScene = new Scene(loader.load());

            Stage stage = (Stage) btnSignin.getScene().getWindow();
            stage.setScene(homeScene);
            stage.centerOnScreen();
            stage.setTitle("Trang chủ - Hệ thống quản lý thư viện");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblErrors.setText("Lỗi khi mở trang chủ");
        }
    }

    private void openSignupForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/signup_form.fxml"));
            Scene signupScene = new Scene(loader.load());
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Đăng ký - Hệ thống quản lý thư viện");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblErrors.setText("Không thể mở form đăng ký");
        }
    }
}

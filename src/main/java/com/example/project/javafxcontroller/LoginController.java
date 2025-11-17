package com.example.project.javafxcontroller;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.ReaderRepository;
import com.example.project.repository.LibrarianRepository;
import com.example.project.util.SessionManager;
import com.example.project.util.SpringFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private SpringFxmlLoader fxmlLoader;

    @FXML
    public void initialize() {
        btnSignin.setOnAction(event -> handleLogin());
        btnSignup.setOnAction(event -> onSignup());
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
            Librarian librarian = librarianOpt.get();
            SessionManager.setCurrentLibrarian(librarian);
            lblErrors.setText("");
            System.out.println("Đăng nhập thành công (Thủ thư): " + librarian.getFullName());
            openHomePage();
            return;
        }

        Optional<Reader> readerOpt = readerRepository.findByUsernameAndPassword(usernameOrEmail, password);
        if (readerOpt.isEmpty()) {
            readerOpt = readerRepository.findByEmailAndPassword(usernameOrEmail, password);
        }

        if (readerOpt.isPresent()) {
            Reader reader = readerOpt.get();
//            SessionManager.setCurrentReader(reader);
            lblErrors.setText("");
            System.out.println("Đăng nhập thành công (Bạn đọc): " + reader.getFullName());
            openReaderHomePage();
            return;
        }

        lblErrors.setText("Tên đăng nhập hoặc mật khẩu không đúng");
    }

    private void openHomePage() {
        try {
            Parent root = fxmlLoader.load("/com/example/project/home_form.fxml");
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

    private void openReaderHomePage() {
        try {
            Parent root = fxmlLoader.load("/com/example/project/reader_home_form.fxml");
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
            Parent root = fxmlLoader.load("/com/example/project/signup_form.fxml");
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

package com.example.project.controller;

import com.example.project.models.User;
import com.example.project.model_controller.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtStudentId;
    @FXML private TextField txtGender;
    @FXML private TextField txtBirthDate;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtEmail;
    @FXML private TextField txtIdCardNumber;
    @FXML private TextField txtPlaceOfBirth;
    @FXML private TextField txtIssuedPlace;
    @FXML private TextField txtMajor;
    @FXML private TextField txtWorkPlace;
    @FXML private TextField txtAddress;

    private final UserController userController = new UserController();

    @FXML
    private void handleBackToLogin(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login_form.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String fullName = txtFullName.getText().trim();
        String studentId = txtStudentId.getText().trim();
        String gender = txtGender.getText().trim();
        String birthDate = txtBirthDate.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String email = txtEmail.getText().trim();
        String idCardNumber = txtIdCardNumber.getText().trim();
        String placeOfBirth = txtPlaceOfBirth.getText().trim();
        String issuedPlace = txtIssuedPlace.getText().trim();
        String major = txtMajor.getText().trim();
        String workPlace = txtWorkPlace.getText().trim();
        String address = txtAddress.getText().trim();

        // Kiểm tra các trường bắt buộc
        if(studentId.isEmpty() || fullName.isEmpty() || gender.isEmpty() || birthDate.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ các thông tin bắt buộc!");
            return;
        }

        // Tạo đối tượng User
        String[] parts = {studentId, fullName, gender, birthDate, phoneNumber, email, idCardNumber, placeOfBirth, issuedPlace, major, workPlace, address};
        User newUser = new User(parts);

        userController.addUser(newUser);
        showAlert("Đăng ký thành công!");
        clearFields();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        alert.showAndWait();
    }

    private void clearFields() {
        txtFullName.clear();
        txtStudentId.clear();
        txtGender.clear();
        txtBirthDate.clear();
        txtPhoneNumber.clear();
        txtEmail.clear();
        txtIdCardNumber.clear();
        txtPlaceOfBirth.clear();
        txtIssuedPlace.clear();
        txtMajor.clear();
        txtWorkPlace.clear();
        txtAddress.clear();
    }
}

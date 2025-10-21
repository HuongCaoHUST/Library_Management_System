package com.example.project.controller;
import com.example.project.model_controller.UserController;
import com.example.project.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HomeControllerForStudent {

    @FXML private Label lblTen, lblMSSV, lblGioitinh, lblNgaysinh, lblSodienthoai,
            lblEmail, lblNoisinh, lblDiachi, lblCCCD, lblNoicap, lblChuyenmon,
            lblNoicongtac, notificationBadge;

    private final UserController userController = new UserController();

    public void loadUserByStudentID(String studentID) {
        User user = userController.getUserById(studentID);
        if (user == null) {
            showAlert("Không tìm thấy MSSV: " + studentID);
            return;
        }

        // Gán dữ liệu lên Label
        lblMSSV.setText(user.getStudentId());
        lblTen.setText(user.getFullName());
        lblGioitinh.setText(user.getGender());
        lblNgaysinh.setText(user.getBirthDate());
        lblSodienthoai.setText(user.getPhoneNumber());
        lblEmail.setText(user.getEmail());
        lblCCCD.setText(user.getIdCardNumber());
        lblNoisinh.setText(user.getPlaceOfBirth());
        lblNoicap.setText(user.getIssuedPlace());
        lblChuyenmon.setText(user.getMajor());
        lblNoicongtac.setText(user.getWorkPlace());
        lblDiachi.setText(user.getAddress());
    }


    protected  void showAlert(String message) {
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

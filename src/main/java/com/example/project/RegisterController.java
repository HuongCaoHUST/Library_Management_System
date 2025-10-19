package com.example.project;

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterController {
    @FXML private TextField txtTen; // Ho va ten
    @FXML private TextField txtMSSV; // MSSV
    @FXML private TextField txtGioitinh; // Giới tính
    @FXML private TextField txtNgaysinh; // Ngày sinh
    @FXML private TextField txtSodienthoai; // Số điện thoại
    @FXML private TextField txtEmail; // Email
    @FXML private TextField txtCCCD; // Số CCCD
    @FXML private TextField txtNoisinh; // Nơi sinh
    @FXML private TextField txtNoicap; // Nơi cấp
    @FXML private TextField txtChuyenmon; // Chuyên môn
    @FXML private TextField txtNoicongtac; // Nơi công tác
    @FXML private TextField txtDiachi; // Địa chỉ liên hệ
    @FXML private TextField txtMatkhau;
    @FXML private TextField txtNhaplaimatkhau;
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
        String hovaten = txtTen.getText().trim();
        String mssv = txtMSSV.getText().trim();
        String gender = txtGioitinh.getText().trim();
        String birthDate = txtNgaysinh.getText().trim();
        String phone = txtSodienthoai.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtNoisinh.getText().trim();
        String birthPlace = txtNgaysinh.getText().trim();
        String issuedPlace = txtNoicap.getText().trim();
        String specialty = txtChuyenmon.getText().trim();
        String workPlace = txtNoicongtac.getText().trim();
        String address = txtDiachi.getText().trim();
        String username = txtMatkhau.getText().trim();
        String password = txtNhaplaimatkhau.getText().trim();

        if(mssv.isEmpty() || gender.isEmpty() || birthDate.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ các thông tin bắt buộc!");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(String.join(",", mssv, gender, birthDate, phone, email, cccd, birthPlace, issuedPlace, specialty, workPlace, address, username, password,"pending"));
            writer.newLine();
            showAlert("Đăng ký thành công!");
            clearFields();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu dữ liệu!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo/logo_HUB.png")));
        alert.showAndWait();
    }

    private void clearFields() {
        txtTen.clear();
        txtMSSV.clear();
        txtGioitinh.clear();
        txtNgaysinh.clear();
        txtSodienthoai.clear();
        txtEmail.clear();
        txtCCCD.clear();
        txtNoisinh.clear();
        txtNoicap.clear();
        txtChuyenmon.clear();
        txtNoicongtac.clear();
        txtDiachi.clear();
        txtMatkhau.clear();
        txtNhaplaimatkhau.clear();
    }
}

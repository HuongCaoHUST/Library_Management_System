package com.example.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HomeController {

    @FXML private Label lblTen;
    @FXML private Label lblMSSV;
    @FXML private Label lblGioitinh;
    @FXML private Label lblNgaysinh;
    @FXML private Label lblSodienthoai;
    @FXML private Label lblEmail;
    @FXML private Label lblNoisinh;
    @FXML private Label lblDiachi;
    @FXML private Label lblCCCD;
    @FXML private Label lblNoicap;
    @FXML private Label lblChuyenmon;
    @FXML private Label lblNoicongtac;
    public void loadUserByMSSV(String mssv) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 11) {
                    String fileMSSV = parts[0].trim();
                    if (fileMSSV.equals(mssv)) {
                        lblMSSV.setText(parts[0].trim());
                        lblTen.setText(parts[1].trim());
                        lblGioitinh.setText(parts[2].trim());
                        lblNgaysinh.setText(parts[3].trim());
                        lblSodienthoai.setText(parts[4].trim());
                        lblEmail.setText(parts[5].trim());
                        lblCCCD.setText(parts[6].trim());
                        lblNoisinh.setText(parts[7].trim());
                        lblNoicap.setText(parts[8].trim());
                        lblChuyenmon.setText(parts[9].trim());
                        lblNoicongtac.setText(parts[10].trim());
                        lblDiachi.setText(parts[11].trim());
                        return;
                    }
                }
            }
            showAlert("Không tìm thấy thông tin người dùng!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi đọc file users.txt!");
        }
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

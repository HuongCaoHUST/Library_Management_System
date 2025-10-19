package com.example.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    @FXML private Label notificationBadge;
    public void loadUserByMSSV(String mssv) {
        try (BufferedReader reader = new BufferedReader(new FileReader("./data/users.txt"))) {
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

    public void updateNotificationBadge() {
        Path filePath = Paths.get("./data/register_queue.txt");

        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                int count = lines.size();
                if (count > 0) {
                    notificationBadge.setText(String.valueOf(count));
                    notificationBadge.setVisible(true);
                } else {
                    notificationBadge.setVisible(false);
                }
            } else {
                notificationBadge.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            notificationBadge.setVisible(false);
        }
    }
}

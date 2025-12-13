package com.example.project.javafxcontroller;

import com.example.project.apiservice.LibrarianApiService;
import com.example.project.dto.ApiResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ChangePasswordController {

    @FXML
    private TextField txtOldPassword;

    @FXML
    private TextField txtNewPassword;

    @FXML
    private TextField txtNewPasswordAgain;

    @FXML
    private void onChangePassword() throws Exception {
        String oldPassword = txtOldPassword.getText();
        String newPassword = txtNewPassword.getText();
        String newPasswordAgain = txtNewPasswordAgain.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || newPasswordAgain.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!newPassword.equals(newPasswordAgain)) {
            showAlert("Lỗi", "Mật khẩu mới không khớp!");
            return;
        }

        LibrarianApiService api = new LibrarianApiService();
        ApiResponse<Void> response = api.changeMyPassword(oldPassword, newPassword);
        if (response.isSuccess()) {
            showAlert("Thành công", "Đổi mật khẩu thành công!");
            clearFields();
        } else {
            showAlert("Lỗi", "Đổi mật khẩu thất bại: " + response.getMessage());
        }
    }

    private void clearFields() {
        txtOldPassword.clear();
        txtNewPassword.clear();
        txtNewPasswordAgain.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

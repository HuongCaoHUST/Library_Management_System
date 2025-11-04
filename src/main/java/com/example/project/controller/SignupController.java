package com.example.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;

@Component
public class SignupController {

    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtEmail;
    @FXML private TextField txtIdCardNumber;
    @FXML private TextField txtPlaceOfBirth;
    @FXML private TextField txtIssuedPlace;
    @FXML private TextField txtMajor;
    @FXML private TextField txtWorkPlace;
    @FXML private TextField txtAddress;
    @FXML private Button btnSignup;

    @Autowired
    private ReaderService readerService;

    @FXML
    private void initialize() {
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue("Nam");
    }

    @FXML
    private void onSignup() {
        try {
            // Validate bắt buộc
            if (txtFullName.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập họ và tên!");
                return;
            }
            if (txtEmail.getText().trim().isEmpty() || !txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không hợp lệ!");
                return;
            }
            if (!txtIdCardNumber.getText().matches("\\d{12}")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số CCCD phải có đúng 12 chữ số!");
                return;
            }

            Reader reader = Reader.builder()
                    .fullName(txtFullName.getText().trim())
                    .gender(cbGender.getValue())
                    .birthDate(dpBirthDate.getValue())
                    .phoneNumber(txtPhoneNumber.getText().trim())
                    .email(txtEmail.getText().trim())
                    .idCardNumber(txtIdCardNumber.getText().trim())
                    .placeOfBirth(txtPlaceOfBirth.getText().trim())
                    .issuedPlace(txtIssuedPlace.getText().trim())
                    .major(txtMajor.getText().trim())
                    .workPlace(txtWorkPlace.getText().trim())
                    .address(txtAddress.getText().trim())
                    .build();

            readerService.registerReader(reader);

            showAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Đăng ký thành công!\n\n" +
                            "Tài khoản của bạn đang chờ duyệt.\n" +
                            "Mật khẩu sẽ được gửi qua email sau khi được duyệt.");

            clearForm();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearForm() {
        txtFullName.clear();
        cbGender.setValue("Nam");
        dpBirthDate.setValue(null);
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

package com.example.project.javafxcontroller;

import com.example.project.apiservice.ReaderApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.RegisterRequest;
import com.example.project.model.Reader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.example.project.service.ReaderService;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

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

    @FXML
    public void initialize() {
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue("Nam");
    }

    @FXML
    private void onSignup() {
        if (!validateForm()) {
            return;
        }

        RegisterRequest dto = buildRegisterDto();

        ReaderApiService api = new ReaderApiService();
        try {
            ApiResponse<Reader> response = api.registerReader(dto);

            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", response.getMessage());
                clearForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Không thành công", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối tới server"
            );
        }
    }

    private static final Map<String, String> GENDER_MAP = Map.of(
            "Nam", "MALE",
            "Nữ", "FEMALE"
    );

    protected RegisterRequest buildRegisterDto() {

        RegisterRequest dto = new RegisterRequest();

        dto.setFullName(txtFullName.getText().trim());
        dto.setGender(GENDER_MAP.get(cbGender.getValue()));
        dto.setBirthDate(dpBirthDate.getValue());
        dto.setPhoneNumber(txtPhoneNumber.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setIdCardNumber(txtIdCardNumber.getText().trim());
        dto.setPlaceOfBirth(txtPlaceOfBirth.getText().trim());
        dto.setIssuedPlace(txtIssuedPlace.getText().trim());
        dto.setRole("READER");
        dto.setMajor(txtMajor.getText().trim());
        dto.setWorkPlace(txtWorkPlace.getText().trim());
        dto.setAddress(txtAddress.getText().trim());

        return dto;
    }

    protected boolean validateForm() {

        if (txtFullName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập họ và tên!");
            return false;
        }

        if (txtEmail.getText().trim().isEmpty()
                || !txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không hợp lệ!");
            return false;
        }

        if (!txtIdCardNumber.getText().matches("\\d{12}")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số CCCD phải có đúng 12 chữ số!");
            return false;
        }

        if (cbGender.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn giới tính!");
            return false;
        }

        if (dpBirthDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày sinh!");
            return false;
        }
        return true;
    }

    @FXML
    private void handleBackToLogin(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/project/login.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Đăng nhập");
        stage.centerOnScreen();
        stage.show();
    }

    protected void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    protected void clearForm() {
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

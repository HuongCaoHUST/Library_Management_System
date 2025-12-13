package com.example.project.javafxcontroller;

import com.example.project.apiservice.ReaderApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.ReaderRegisterRequest;
import com.example.project.model.Reader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.example.project.service.ReaderService;
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

    private ReaderService readerService;


    @FXML
    private void initialize() {
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue("Nam");
    }

    @FXML
    private void onSignup() {
        if (!validateForm()) {
            return;
        }

        ReaderRegisterRequest dto = buildRegisterDto();

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

    private ReaderRegisterRequest buildRegisterDto() {

        ReaderRegisterRequest dto = new ReaderRegisterRequest();

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

    private boolean validateForm() {

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
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        Parent root = springFxmlLoader.load("/com/example/project/login.fxml");
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
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

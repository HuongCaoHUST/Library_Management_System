package com.example.project.javafxcontroller;

import com.example.project.apiservice.DocumentApiService;
import com.example.project.apiservice.SupplierApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.model.Document;
import com.example.project.model.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SupplierAddController {

    @FXML private TextField txtSupplierName;
    @FXML private TextField txtContactPerson;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onAddSupplier() {
        if (!validateForm()) {
            return;
        }
        SupplierRequest dto = buildSupplierDto();
        SupplierApiService api = new SupplierApiService();
        try {
            ApiResponse<Supplier> response = api.addSupplier(dto);

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

    protected SupplierRequest buildSupplierDto() {

        SupplierRequest dto = new SupplierRequest();

        dto.setSupplierName(txtSupplierName.getText().trim());
        dto.setContactPerson(txtContactPerson.getText().trim());
        dto.setPhoneNumber(txtPhoneNumber.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setAddress(txtAddress.getText().trim());
        return dto;
    }

    protected boolean validateForm() {

        if (txtSupplierName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên nhà cung cấp!");
            return false;
        }

        if (txtContactPerson.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập người đại diện!");
            return false;
        }
        return true;
    }

    protected void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    protected void clearForm() {
        txtSupplierName.clear();
        txtContactPerson.clear();
        txtPhoneNumber.clear();
        txtEmail.clear();
        txtAddress.clear();
    }
}

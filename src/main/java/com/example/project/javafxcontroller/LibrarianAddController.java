package com.example.project.javafxcontroller;

import com.example.project.apiservice.LibrarianApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.RegisterRequest;
import com.example.project.model.Librarian;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class LibrarianAddController extends SignupController {

    @FXML
    public void initialize() {
        super.initialize();
    }

    @FXML
    private void onAddLibrarian() {
        if (!validateForm()) {
            return;
        }

        RegisterRequest dto = buildRegisterDto();

        LibrarianApiService api = new LibrarianApiService();
        try {
            ApiResponse<Librarian> response = api.registerLibrarian(dto);

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
}

package com.example.project.javafxcontroller;

import com.example.project.apiservice.DocumentApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.model.Document;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DocumentAddController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private ComboBox<String> cbPublisher;
    @FXML private TextField txtPublicationYear;
    @FXML private TextField txtClassificationNumber;
    @FXML private TextField txtCategory;
    @FXML private TextField txtShelfLocation;
    @FXML private ComboBox<String> cbDocumentType;
    @FXML private TextField txtAccessLink;
    @FXML private ComboBox<String> cbStatus;

    @FXML
    public void initialize() {
        cbPublisher.getItems().addAll("NXB A", "NXB B", "NXB C", "NXB D");
        cbDocumentType.getItems().addAll("Tài liệu in", "Tài liệu số");
        cbStatus.getItems().addAll("Được mượn", "Không được mượn");
    }

    @FXML
    private void onAddDocument() {
        if (!validateForm()) {
            return;
        }

        DocumentRequest dto = buildDocumentDto();
        DocumentApiService api = new DocumentApiService();
        try {
            ApiResponse<Document> response = api.addDocument(dto);

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

    protected DocumentRequest buildDocumentDto() {

        DocumentRequest dto = new DocumentRequest();

        dto.setTitle(txtTitle.getText().trim());
        dto.setAuthor(txtAuthor.getText().trim());
        dto.setPublisher(cbPublisher.getValue());
        dto.setPublicationYear(txtPublicationYear.getText().trim());
        dto.setClassificationNumber(txtClassificationNumber.getText().trim());
        dto.setCategory(txtCategory.getText().trim());
        dto.setShelfLocation(txtShelfLocation.getText().trim());
        dto.setDocumentType(cbDocumentType.getValue());
        dto.setAccessLink(txtAccessLink.getText().trim());
        dto.setStatus(cbStatus.getValue());
        dto.setAvailableCopies(0);
        return dto;
    }

    protected boolean validateForm() {

        if (txtTitle.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên tài liệu!");
            return false;
        }

        if (txtAuthor.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên tác giả!");
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
        txtTitle.clear();
        txtAuthor.clear();
        cbPublisher.setValue(null);
        txtPublicationYear.clear();
        txtClassificationNumber.clear();
        txtCategory.clear();
        txtShelfLocation.clear();
        cbDocumentType.setValue(null);
        txtAccessLink.clear();
        cbStatus.setValue(null);
    }
}

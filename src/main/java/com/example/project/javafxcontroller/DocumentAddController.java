package com.example.project.javafxcontroller;

import com.example.project.apiservice.CategoryApiService;
import com.example.project.apiservice.DocumentApiService;
import com.example.project.apiservice.DocumentTypeApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.model.Category;
import com.example.project.model.Document;
import com.example.project.model.DocumentType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class DocumentAddController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private ComboBox<String> cbPublisher;
    @FXML private TextField txtPublicationYear;
    @FXML private TextField txtClassificationNumber;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TextField txtShelfLocation;
    @FXML private ComboBox<String> cbDocumentType;
    @FXML private TextField txtAccessLink;
    @FXML private ComboBox<String> cbStatus;

    @FXML
    private ImageView imgCover;
    private File coverImageFile;

    private CategoryApiService categoryApiService;
    private DocumentTypeApiService documentTypeApiService;

    @FXML
    public void initialize() {
        categoryApiService = new CategoryApiService();
        documentTypeApiService = new DocumentTypeApiService();
        cbPublisher.getItems().addAll("NXB A", "NXB B", "NXB C", "NXB D");
        cbStatus.getItems().addAll("Được mượn", "Không được mượn");
        loadCategories();
        loadDocumentTypes();
    }

    public void loadCategories() {
        try {
            List<Category> categories = categoryApiService.getCategoriesList();

            cbCategory.getItems().setAll(
                    categories.stream()
                            .map(Category::getCategoryName)
                            .toList()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDocumentTypes() {
        try {
            List<DocumentType> documentTypes = documentTypeApiService.getDocumentTypesList();

            cbDocumentType.getItems().setAll(
                    documentTypes.stream()
                            .map(DocumentType::getDocumentTypeName)
                            .toList()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                Document document = response.getData();
                Long documentId = document.getDocumentId();

                if (coverImageFile != null) {
                    api.uploadDocumentCover(documentId, coverImageFile);
                }

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
        dto.setCategoryName(cbCategory.getValue());
        dto.setShelfLocation(txtShelfLocation.getText().trim());
        dto.setDocumentTypeName(cbDocumentType.getValue());
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

    @FXML
    private void onUploadCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh bìa");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File file = fileChooser.showOpenDialog(imgCover.getScene().getWindow());

        if (file != null) {
            coverImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgCover.setImage(image);
        }
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
        cbCategory.setValue(null);
        txtShelfLocation.clear();
        cbDocumentType.setValue(null);
        txtAccessLink.clear();
        cbStatus.setValue(null);
    }
}

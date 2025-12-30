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

public class DocumentAddFromFileController {

    @FXML private Button btnDownloadTemplate;
    @FXML private Button btnUploadExcel;
    @FXML private Label lblFilePath;
    @FXML private Button btnSubmit;

    private File selectedExcelFile;

    @FXML
    public void initialize() {

    }

    @FXML
    private void downloadTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel mẫu");
        fileChooser.setInitialFileName("document_import_template.xlsx");

        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile == null) {
            return;
        }

        DocumentApiService apiService = new DocumentApiService();
        apiService.downloadImportTemplate(saveFile);
    }

    @FXML
    private void uploadExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file Excel (.xlsx)");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel File (*.xlsx)", "*.xlsx")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            showAlert(Alert.AlertType.ERROR,"Lỗi","Chỉ chấp nhận file Excel định dạng .xlsx");
            return;
        }

        selectedExcelFile = file;
        lblFilePath.setText(file.getAbsolutePath());
    }

    @FXML
    private void submitData() {
        if (selectedExcelFile == null) {
            showAlert(Alert.AlertType.ERROR,"Lỗi","Vui lòng chọn file Excel");
            return;
        }

        if (!selectedExcelFile.getName().toLowerCase().endsWith(".xlsx")) {
            showAlert(Alert.AlertType.ERROR,"Lỗi","Chỉ chấp nhận file .xlsx");
            return;
        }

        try {
            DocumentApiService apiService = new DocumentApiService();
            ApiResponse<String> response =
                    apiService.importDocuments(selectedExcelFile);

            if (response != null && response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION,"Thành công","Upload thành công");
            } else {
                showAlert(Alert.AlertType.ERROR,"Lỗi", response != null ? response.getMessage() : "Upload thất bại");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Lỗi","Lỗi khi upload file: " + e.getMessage());
        }
    }

    protected void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

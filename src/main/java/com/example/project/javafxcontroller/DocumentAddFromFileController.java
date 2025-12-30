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

    @FXML
    public void initialize() {

    }

    @FXML
    private void downloadTemplate() {

    }

    @FXML
    private void uploadExcel() {

    }

    @FXML
    private void submitData() {

    }

    protected void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

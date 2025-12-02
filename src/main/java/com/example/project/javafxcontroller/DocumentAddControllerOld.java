package com.example.project.javafxcontroller;

import com.example.project.model.Document;
import com.example.project.service.DocumentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentAddControllerOld {

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtPublisher;
    @FXML private TextField txtYear;
    @FXML private TextField txtClassification;
    @FXML private TextField txtCategory;
    @FXML private TextField txtShelf;
    @FXML private ComboBox<String> cbType;
    @FXML private TextField txtAccessLink;
    @FXML private ComboBox<String> cbStatus;

    @Autowired
    private DocumentService documentService;

    @FXML
    public void initialize() {
        cbType.getItems().addAll("Tài liệu In", "Tài liệu Số");
        cbStatus.getItems().addAll("Được mượn", "Không được mượn");
        txtAccessLink.setVisible(false);

        cbType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            if ("Tài liệu In".equalsIgnoreCase(newVal.trim())) {
                txtAccessLink.setVisible(false);
                txtAccessLink.clear();
            } else if ("Tài liệu Số".equalsIgnoreCase(newVal.trim())) {
                txtAccessLink.setVisible(true);
            }
        });
    }

    @FXML
    private void onSaveDocument() {
        try {
            Document doc = Document.builder()
                    .title(txtTitle.getText())
                    .author(txtAuthor.getText())
                    .publisher(txtPublisher.getText())
                    .publicationYear(Integer.parseInt(txtYear.getText()))
                    .classificationNumber(txtClassification.getText())
                    .category(txtCategory.getText())
                    .shelfLocation(txtShelf.getText())
                    .documentType(cbType.getValue())
                    .accessLink(txtAccessLink.getText())
                    .status(cbStatus.getValue())
                    .build();

            documentService.save(doc);
            showAlert("Thành công", "Đã thêm tài liệu mới!");
            closeForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể thêm tài liệu: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

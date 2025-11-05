package com.example.project.controller;
import com.example.project.model.Document;
import com.example.project.service.DocumentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class DocumentListController implements Initializable {

    @FXML private TableView<Document> tableView;
    @FXML private TableColumn<Document, String> colTitle;
    @FXML private TableColumn<Document, Long> colDocumentId;
    @FXML private TableColumn<Document, String> colAuthor;
    @FXML private TableColumn<Document, String> colShelfLocation;
    @FXML private TableColumn<Document, String> colDocumentType;
    @FXML private TableColumn<Document, Void> colDetail;

    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @Autowired
    private DocumentService documentService;

    private ObservableList<Document> masterData; // Dữ liệu gốc (PENDING)
    private ObservableList<Document> filteredData; // Dữ liệu sau tìm kiếm

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadDocuments();
//        setupSearch();
    }

    private void setupTableColumns() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colShelfLocation.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
        colDocumentType.setCellValueFactory(new PropertyValueFactory<>("documentType"));

        // Detail Col
        colDetail.setCellFactory(tc -> new TableCell<>() {
            private final Button detailBtn = createButton("Xem", "#4CAF50");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow() == null || getTableRow().getItem() == null ? null : detailBtn);
            }

            {
                detailBtn.setOnAction(e -> {
                    Document reader = getTableView().getItems().get(getIndex());
//                    showDetailDialog(reader);
                });
            }
        });
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10;"
                , color));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    private void loadDocuments() {
        List<Document> documents = documentService.findAll();
        masterData = FXCollections.observableArrayList(documents);
        filteredData = FXCollections.observableArrayList(masterData);
        tableView.setItems(filteredData);
    }

//    private void setupSearch() {
//        searchField.textProperty().addListener((obs, oldText, newText) -> filterTable(newText));
//        searchButton.setOnAction(e -> filterTable(searchField.getText()));
//    }

//    private void filterTable(String keyword) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            filteredData.setAll(masterData);
//        } else {
//            String lowerCaseKeyword = keyword.toLowerCase();
//            List<Document> filtered = masterData.stream()
//                    .filter(r ->
//                            r.getFullName().toLowerCase().contains(lowerCaseKeyword)
//                    )
//                    .collect(Collectors.toList());
//            filteredData.setAll(filtered);
//        }
//    }

//    private void showDetailDialog(Document reader) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Chi tiết Reader");
//        alert.setHeaderText(reader.getFullName() + " - " + reader.getUserId());
//
//        String content = String.format(
//                "Họ và tên: %s\n" +
//                        "MSSV: %s\n" +
//                        "Ngày sinh: %s\n" +
//                        "CCCD: %s\n" +
//                        "Nơi công tác: %s\n" +
//                        "Trạng thái: %s",
//                reader.getFullName(),
//                reader.getUserId(),
//                reader.getBirthDate(),
//                reader.getIdCardNumber(),
//                reader.getWorkPlace(),
//                reader.getStatus()
//        );
//
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
}
package com.example.project.controller;
import com.example.project.model.Document;
import com.example.project.model.Reader;
import com.example.project.service.DocumentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
    @FXML private TableColumn<Document, String> colPublisher;
    @FXML private TableColumn<Document, String> colShelfLocation;
    @FXML private TableColumn<Document, String> colDocumentType;
    @FXML private TableColumn<Document, String> colAvailableCopies;
    @FXML private TableColumn<Document, String> colBorrowedCopies;
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
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colShelfLocation.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
        colDocumentType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        colAvailableCopies.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        colBorrowedCopies.setCellValueFactory(new PropertyValueFactory<>("borrowedCopies"));

        String cellStyle = "-fx-alignment: CENTER;-fx-font-family: 'Segoe UI Regular'; -fx-font-size: 15px;";
        colTitle.setStyle(cellStyle);
        colDocumentId.setStyle(cellStyle);
        colAuthor.setStyle(cellStyle);
        colPublisher.setStyle(cellStyle);
        colShelfLocation.setStyle(cellStyle);
        colDocumentType.setStyle(cellStyle);
        colAvailableCopies.setStyle(cellStyle);
        colBorrowedCopies.setStyle(cellStyle);

        // Detail Col
        colDetail.setCellFactory(tc -> new TableCell<>() {
            private final Button detailBtn = createButton("Xem", "#4CAF50");
            private final HBox container = new HBox(detailBtn);
            {
                container.setAlignment(Pos.CENTER);
                detailBtn.setOnAction(e -> {
                    Document document = getTableView().getItems().get(getIndex());
//                    showDetailDialog(reader);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }

            {
                detailBtn.setOnAction(e -> {
                    Document document = getTableView().getItems().get(getIndex());
//                    showDetailDialog(reader);
                });
            }
        });
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setRowFactory(tv -> new TableRow<>() {
            {
                setPrefHeight(50);
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
}
package com.example.project.javafxcontroller;
import com.example.project.model.Document;
import com.example.project.apiservice.DocumentApiService;
import com.example.project.service.DocumentService;
import com.example.project.util.SpringFxmlLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    @FXML private ComboBox<String> documentTypeComboBox;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SpringFxmlLoader fxmlLoader;

    @Autowired
    private DocumentApiService documentApiService;
    private Stage loadingStage;

    private final ObservableList<Document> documentList = FXCollections.observableArrayList();
    private Timeline debounceTimeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        tableView.setItems(documentList);
        setupComboBox();
        searchDocuments();
        setupSearch();
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
                    showDetailDialog(document);
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
                    showDetailDialog(document);
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

    private void showDetailDialog(Document document) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/document_detail_form.fxml");
            Stage stage = new Stage();
            DocumentDetailController controller = (DocumentDetailController) root.getUserData();
            controller.setDocument(document);
            stage.setTitle("Chi tiết bạn đọc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComboBox() {
        documentTypeComboBox.setItems(FXCollections.observableArrayList("Tất cả", "Tài liệu In", "Tài liệu Số"));
        documentTypeComboBox.setOnAction(e -> searchDocuments());
    }

    private void searchDocuments() {
        String keyword = searchField.getText().trim();
        String title = keyword.isEmpty() ? null : keyword;

        String documentType = documentTypeComboBox.getValue();
        if ("Tất cả".equals(documentType)) documentType = null;

        showLoadingPopup("Đang tải danh sách bạn đọc...");

        String finalDocumentType = documentType;

        Task<List<Document>> task = new Task<>() {
            @Override
            protected List<Document> call() throws Exception {
                return documentApiService.filterDocuments(title, null, null, finalDocumentType, null);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Document> result = task.getValue();
            documentList.setAll(result != null ? result : List.of());
            tableView.refresh();

            hideLoadingPopup();

            if (result == null || result.isEmpty()) {
                tableView.setPlaceholder(new Label("Không tìm thấy bạn đọc nào."));
            } else {
                tableView.setPlaceholder(new Label(""));
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            hideLoadingPopup();
            Throwable ex = task.getException();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Không kết nối được server!\n" + ex.getMessage());
            alert.show();
            tableView.setPlaceholder(new Label("Lỗi tải dữ liệu..."));
        }));

        new Thread(task).start();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldText, newText) -> debounceSearch());
        searchButton.setOnAction(e -> searchDocuments());
    }

    private void debounceSearch() {
        if (debounceTimeline != null) debounceTimeline.stop();
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> searchDocuments()));
        debounceTimeline.play();
    }

    private void showLoadingPopup(String message) {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label(message);
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane(box);

        Scene scene = new Scene(root, 300, 200);
        scene.setFill(Color.TRANSPARENT);
        loadingStage.setScene(scene);
        loadingStage.sizeToScene();

        Platform.runLater(() -> {
            Stage owner = (Stage) tableView.getScene().getWindow();
            if (owner != null) {
                loadingStage.initOwner(owner);
                loadingStage.initModality(Modality.APPLICATION_MODAL);
            }
            loadingStage.show();
        });
    }

    private void hideLoadingPopup() {
        if (loadingStage != null && loadingStage.isShowing()) {
            loadingStage.close();
            loadingStage = null;
        }
    }
}
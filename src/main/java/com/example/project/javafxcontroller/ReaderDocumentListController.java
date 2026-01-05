package com.example.project.javafxcontroller;
import com.example.project.apiservice.DocumentTypeApiService;
import com.example.project.model.BorrowItem;
import com.example.project.model.Document;
import com.example.project.apiservice.DocumentApiService;
import com.example.project.model.DocumentType;
import com.example.project.security.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ReaderDocumentListController {

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
    @FXML private TableColumn<Document, Void> colAddToBorrowSlip;
    @FXML private Button btnDeleteBorrowSlip;
    @FXML private Button btnConfirmBorrowSlip;
    private final ObservableList<BorrowItem> cartItems = FXCollections.observableArrayList();

    @FXML private TableView<BorrowItem> cartTableView;
    @FXML private TableColumn<BorrowItem, Number> stt;
    @FXML private TableColumn<BorrowItem, String> cartColTitle;
    @FXML private TableColumn<BorrowItem, String> cartColAuthor;
    @FXML private TableColumn<BorrowItem, Number> quantity;
    @FXML private TableColumn<BorrowItem, String> cartColBorrowDate;
    @FXML private TableColumn<BorrowItem, String> cartColDueDate;
    @FXML private TableColumn<BorrowItem, Void> cartColRemove;


    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> documentTypeComboBox;
    @FXML private Button addDocumentButton;
    @FXML private Button exportDocumentToExceleButton;

    private DocumentApiService documentApiService;

    private Stage loadingStage;

    private final ObservableList<Document> documentList = FXCollections.observableArrayList();
    private Timeline debounceTimeline;

    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private DocumentTypeApiService documentTypeApiService;

    @FXML
    public void initialize() {
        documentApiService = new DocumentApiService();
        documentTypeApiService = new DocumentTypeApiService();

        UserSession session = UserSession.getInstance();

        if (addDocumentButton != null) {
            addDocumentButton.setVisible(session.hasPermission("DOCUMENT_CREATE"));
        }
        setupTableColumns();
        tableView.setItems(documentList);
        setupComboBox();
        searchDocuments();
        setupSearch();
        setupBorrowSlip();

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
        tableView.setRowFactory(tv -> new TableRow<>() {
            {
                setPrefHeight(50);
            }
        });

        // Col add to borrowSlip
        colAddToBorrowSlip.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("➕ Thêm");
            private final HBox container = new HBox(btn);

            {
                container.setAlignment(Pos.CENTER);
                btn.getStyleClass().add("primary-btn");
                btn.setOnAction(e -> {
                    Document doc = getTableView().getItems().get(getIndex());

                    if (doc.getAvailableCopies() <= 0) {
                        showAlert(Alert.AlertType.ERROR,"Hết sách","Tài liệu đã hết!");
                        return;
                    }
                    addToCart(doc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/project/document_detail_form.fxml"));
            Parent root = loader.load();
            DocumentDetailController controller = loader.getController();

            controller.setDocument(document);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết tài liệu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComboBox() {
        try {
            List<DocumentType> documentTypes = documentTypeApiService.getDocumentTypesList();

            documentTypeComboBox.getItems().setAll(
                    documentTypes.stream()
                            .map(DocumentType::getDocumentTypeName)
                            .toList()
            );
            documentTypeComboBox.setOnAction(e -> searchDocuments());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBorrowSlip() {
        cartTableView.setItems(cartItems);
        stt.setCellFactory(col -> {
            TableCell<BorrowItem, Number> cell = new TableCell<>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        cartColTitle.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDocument().getTitle()
                )
        );

        cartColAuthor.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDocument().getAuthor()
                )
        );

        quantity.setCellValueFactory(data ->
                data.getValue().quantityProperty()
        );
        quantity.setCellFactory(col -> {
            TableCell<BorrowItem, Number> cell = new TableCell<>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        cartColBorrowDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(LocalDate.now().format(dtf))
        );
        cartColBorrowDate.setCellFactory(col -> {
            TableCell<BorrowItem, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        cartColDueDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(LocalDate.now().plusMonths(3).format(dtf))
        );
        cartColDueDate.setCellFactory(col -> {
            TableCell<BorrowItem, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        cartColRemove.setCellFactory(col -> {
            TableCell<BorrowItem, Void> cell = new TableCell<>() {
                private final Button btn = new Button("❌");
                private final HBox container = new HBox(btn);
                {
                    container.setAlignment(Pos.CENTER);
                    btn.setOnAction(e -> {
                        BorrowItem item =
                                getTableView().getItems().get(getIndex());
                        cartItems.remove(item);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : container);
                }
            };
            return cell;
        });
    }

    private void addToCart(Document doc) {

        Optional<BorrowItem> existingItem =
                cartItems.stream()
                        .filter(i -> i.getDocument().getDocumentId().equals(doc.getDocumentId()))
                        .findFirst();

        if (existingItem.isPresent()) {
            BorrowItem item = existingItem.get();

            if (item.quantityProperty().get() < doc.getAvailableCopies()) {
                item.increaseQuantity();
            } else {
                showAlert(Alert.AlertType.ERROR,"Hết sách","Không thể mượn thêm, số lượng đã đạt tối đa");
            }

        } else {
            if (doc.getAvailableCopies() <= 0) {
                showAlert(Alert.AlertType.ERROR,"Hết sách","Tài liệu không còn bản nào");
                return;
            }
            cartItems.add(new BorrowItem(doc));
        }
    }


    private void searchDocuments() {
        String keyword = searchField.getText().trim();
        String title = keyword.isEmpty() ? null : keyword;

        String documentType = documentTypeComboBox.getValue();
        if ("Tất cả".equals(documentType)) documentType = null;

        showLoadingPopup("Đang tải danh sách tài liệu...");

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
                tableView.setPlaceholder(new Label("Không tìm thấy tài liệu nào."));
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

    @FXML
    private void handleDeleteBorrowSlip() {

        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Thông báo",
                    "Phiếu mượn đang trống");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa toàn bộ phiếu mượn?");
        confirm.setContentText("Tất cả tài liệu trong giỏ sẽ bị xóa");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                cartItems.clear();
            }
        });
    }

    @FXML
    protected void openConfirmBorrowSlip(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project/borrow_slip_confirm.fxml")
            );

            Parent root = loader.load();

            BorrowSlipConfirmController controller = loader.getController();

            controller.setCartItems(cartItems);
            controller.setOnSuccessCallback(() -> {
                cartItems.clear();
                searchDocuments();
            });

            Stage stage = new Stage();
            stage.setTitle("Xác nhận phiếu mượn");
            stage.setScene(new Scene(root));

            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL); // chặn form cha

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void addDocument(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/document_add_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm tài liệu");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void addDocumentFromFile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/document_add_from_file_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm tài liệu từ Excel");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportDocumentToExcel(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.setInitialFileName("document_export.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")
        );

        File saveFile = fileChooser.showSaveDialog(
                exportDocumentToExceleButton.getScene().getWindow()
        );

        if (saveFile == null) {
            return;
        }

        try {
            documentApiService.exportDocumentsToExcel(saveFile);
            showAlert(Alert.AlertType.INFORMATION,"Thành công","Xuất file Excel thành công!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Lỗi","Lỗi khi export:" + e.getMessage());
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
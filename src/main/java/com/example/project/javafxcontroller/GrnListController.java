package com.example.project.javafxcontroller;

import com.example.project.apiservice.GrnApiService;
import com.example.project.model.Grn;
import com.example.project.model.GrnDetail;
import com.example.project.util.SpringFxmlLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class GrnListController implements Initializable {

    // ========== FXML Components ==========
    @FXML
    private TableView<Grn> tableView;

    @FXML
    private TableColumn<Grn, String> colReceiptId;

    @FXML
    private TableColumn<Grn, String> colSupplier;

    @FXML
    private TableColumn<Grn, String> colReceiver;

    @FXML
    private TableColumn<Grn, String> colDeliverer;

    @FXML
    private TableColumn<Grn, LocalDate> colReceiveDate;

    @FXML
    private TableColumn<Grn, Integer> colTotalItems;

    @FXML
    private TableColumn<Grn, Void> colDetail;

    @FXML
    private TableColumn<Grn, Void> colDelete;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    // ========== Services ==========
    @Autowired
    private GrnApiService grnApiService;

    @Autowired
    private SpringFxmlLoader fxmlLoader;

    // ========== Data ==========
    private final ObservableList<Grn> grnList = FXCollections.observableArrayList();
    private Stage loadingStage;
    private Timeline debounceTimeline;

    // ========== Date Formatter ==========
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        tableView.setItems(grnList);
        loadAllGrns();
        setupSearch();
    }

    /**
     * Thiết lập các cột cho TableView
     */
    private void setupTableColumns() {
        // Cột Mã phiếu
        colReceiptId.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        colReceiptId.setStyle("-fx-alignment: CENTER;");

        // Cột Đơn vị cung cấp
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colSupplier.setStyle("-fx-alignment: CENTER-LEFT;");

        // Cột Bên nhận
        colReceiver.setCellValueFactory(new PropertyValueFactory<>("receiver"));
        colReceiver.setStyle("-fx-alignment: CENTER-LEFT;");

        // Cột Bên giao
        colDeliverer.setCellValueFactory(new PropertyValueFactory<>("deliverer"));
        colDeliverer.setStyle("-fx-alignment: CENTER-LEFT;");

        // Cột Ngày nhận - Format ngày dd/MM/yyyy
        colReceiveDate.setCellValueFactory(new PropertyValueFactory<>("receiveDate"));
        colReceiveDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DATE_FORMATTER));
                }
            }
        });
        colReceiveDate.setStyle("-fx-alignment: CENTER;");

        // Cột Số lượng tài liệu - Tính từ danh sách items
        colTotalItems.setCellValueFactory(cellData -> {
            Grn grn = cellData.getValue();
            int itemCount = (grn.getItems() != null) ? grn.getItems().size() : 0;
            return new SimpleIntegerProperty(itemCount).asObject();
        });
        colTotalItems.setStyle("-fx-alignment: CENTER;");

        // Cột Chi tiết - Button "Chi tiết"
        setupDetailColumn();

        // Cột Xóa - Button "Xóa"
        setupDeleteColumn();

        // Thiết lập chính sách resize cột
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Thiết lập chiều cao dòng
        tableView.setRowFactory(tv -> new TableRow<>() {
            {
                setPrefHeight(50);
            }
        });

        // Style chung cho các cột
        String cellStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;";
        colReceiptId.setStyle(colReceiptId.getStyle() + cellStyle);
        colSupplier.setStyle(colSupplier.getStyle() + cellStyle);
        colReceiver.setStyle(colReceiver.getStyle() + cellStyle);
        colDeliverer.setStyle(colDeliverer.getStyle() + cellStyle);
        colReceiveDate.setStyle(colReceiveDate.getStyle() + cellStyle);
        colTotalItems.setStyle(colTotalItems.getStyle() + cellStyle);
    }

    /**
     * Thiết lập cột Chi tiết với Button
     */
    private void setupDetailColumn() {
        colDetail.setCellFactory(tc -> new TableCell<>() {
            private final Button detailBtn = createStyledButton("Chi tiết", "#1f3368");
            private final HBox container = new HBox(detailBtn);

            {
                container.setAlignment(Pos.CENTER);
                detailBtn.setOnAction(e -> {
                    Grn grn = getTableView().getItems().get(getIndex());
                    showDetailDialog(grn);
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
        });
    }

    /**
     * Thiết lập cột Xóa với Button
     */
    private void setupDeleteColumn() {
        colDelete.setCellFactory(tc -> new TableCell<>() {
            private final Button deleteBtn = createStyledButton("Xóa", "#a81c29");
            private final HBox container = new HBox(deleteBtn);

            {
                container.setAlignment(Pos.CENTER);
                deleteBtn.setOnAction(e -> {
                    Grn grn = getTableView().getItems().get(getIndex());
                    handleDeleteGrn(grn);
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
        });
    }

    /**
     * Tạo Button với style
     */
    private Button createStyledButton(String text, String backgroundColor) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 13px; " +
                        "-fx-padding: 6 14; " +
                        "-fx-background-radius: 5;",
                backgroundColor));
        btn.setCursor(javafx.scene.Cursor.HAND);

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-opacity: 0.85;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.85;", "")));

        return btn;
    }

    /**
     * Load tất cả phiếu nhập kho từ API
     */
    private void loadAllGrns() {
        showLoadingPopup("Đang tải danh sách phiếu nhập kho...");

        Task<List<Grn>> task = new Task<>() {
            @Override
            protected List<Grn> call() throws Exception {
                return grnApiService.getAllGrns();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Grn> result = task.getValue();
            grnList.setAll(result != null ? result : List.of());
            tableView.refresh();
            hideLoadingPopup();

            if (result == null || result.isEmpty()) {
                tableView.setPlaceholder(createPlaceholderLabel("Chưa có phiếu nhập kho nào."));
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            hideLoadingPopup();
            Throwable ex = task.getException();
            showErrorAlert("Không kết nối được server!\n" + (ex != null ? ex.getMessage() : "Lỗi không xác định"));
            tableView.setPlaceholder(createPlaceholderLabel("Lỗi tải dữ liệu..."));
        }));

        new Thread(task).start();
    }

    /**
     * Tìm kiếm phiếu nhập kho theo mã phiếu
     */
    private void searchGrns() {
        String keyword = searchField.getText().trim();

        showLoadingPopup("Đang tìm kiếm...");

        Task<List<Grn>> task = new Task<>() {
            @Override
            protected List<Grn> call() throws Exception {
                return grnApiService.searchGrnsByReceiptId(keyword);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Grn> result = task.getValue();
            grnList.setAll(result != null ? result : List.of());
            tableView.refresh();
            hideLoadingPopup();

            if (result == null || result.isEmpty()) {
                tableView.setPlaceholder(createPlaceholderLabel("Không tìm thấy phiếu nhập kho nào."));
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            hideLoadingPopup();
            Throwable ex = task.getException();
            showErrorAlert("Không thể tìm kiếm!\n" + (ex != null ? ex.getMessage() : "Lỗi không xác định"));
        }));

        new Thread(task).start();
    }

    /**
     * Thiết lập chức năng tìm kiếm với debounce
     */
    private void setupSearch() {
        // Tìm kiếm khi nhấn Enter hoặc click button
        searchButton.setOnAction(e -> searchGrns());

        // Debounce search khi gõ
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            debounceSearch();
        });

        // Tìm kiếm khi nhấn Enter trong TextField
        searchField.setOnAction(e -> searchGrns());
    }

    /**
     * Debounce để tránh gọi API liên tục khi gõ
     */
    private void debounceSearch() {
        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> searchGrns()));
        debounceTimeline.play();
    }

    /**
     * Hiển thị dialog chi tiết phiếu nhập kho
     */
    private void showDetailDialog(Grn grn) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết phiếu nhập kho");
        alert.setHeaderText("Mã phiếu: " + grn.getReceiptId());

        // Tạo nội dung chi tiết
        StringBuilder content = new StringBuilder();
        content.append("━━━━━━━━━━ THÔNG TIN CHUNG ━━━━━━━━━━\n\n");
        content.append("📦 Đơn vị cung cấp: ").append(grn.getSupplier()).append("\n");
        content.append("👤 Bên nhận: ").append(grn.getReceiver()).append("\n");
        content.append("🚚 Bên giao: ").append(grn.getDeliverer()).append("\n");
        content.append("📅 Ngày nhận: ").append(grn.getReceiveDate().format(DATE_FORMATTER)).append("\n");
        content.append("📚 Số lượng tài liệu: ").append(grn.getItems() != null ? grn.getItems().size() : 0).append("\n\n");

        content.append("━━━━━━━━━━ DANH SÁCH TÀI LIỆU ━━━━━━━━━━\n\n");

        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            NumberFormat currencyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            int index = 1;
            for (GrnDetail item : grn.getItems()) {
                content.append(String.format("%d. %s\n", index++, item.getTitle()));
                content.append(String.format("   • Tác giả: %s\n", item.getAuthor() != null ? item.getAuthor() : "N/A"));
                content.append(String.format("   • Nhà xuất bản: %s\n", item.getPublisher() != null ? item.getPublisher() : "N/A"));
                content.append(String.format("   • Thể loại: %s\n", item.getCategory() != null ? item.getCategory() : "N/A"));
                content.append(String.format("   • Vị trí kệ: %s\n", item.getShelfLocation()));
                content.append(String.format("   • Số lượng: %d bản\n", item.getAvailableCopies()));
                if (item.getCoverPrice() != null) {
                    content.append(String.format("   • Đơn giá: %s đ\n", currencyFormatter.format(item.getCoverPrice())));
                }
                content.append("\n");
            }
        } else {
            content.append("Không có tài liệu nào trong phiếu này.\n");
        }

        // Tạo TextArea để hiển thị nội dung (có thể scroll)
        TextArea textArea = new TextArea(content.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);
        textArea.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(550);
        alert.showAndWait();
    }

    /**
     * Xử lý xóa phiếu nhập kho
     */
    private void handleDeleteGrn(Grn grn) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa phiếu nhập kho");
        confirm.setContentText("Bạn có chắc muốn xóa phiếu: " + grn.getReceiptId() + "?\n\n" +
                "⚠️ Lưu ý: Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoadingPopup("Đang xóa phiếu nhập kho...");

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return grnApiService.deleteGrnByReceiptId(grn.getReceiptId());
                }
            };

            task.setOnSucceeded(e -> Platform.runLater(() -> {
                hideLoadingPopup();
                if (task.getValue()) {
                    showInfoAlert("Đã xóa phiếu thành công!");
                    loadAllGrns(); // Refresh list
                } else {
                    showErrorAlert("Không thể xóa phiếu! Vui lòng thử lại.");
                }
            }));

            task.setOnFailed(e -> Platform.runLater(() -> {
                hideLoadingPopup();
                Throwable ex = task.getException();
                showErrorAlert("Lỗi khi xóa: " + (ex != null ? ex.getMessage() : "Lỗi không xác định"));
            }));

            new Thread(task).start();
        }
    }

    /**
     * Hiển thị popup loading
     */
    private void showLoadingPopup(String message) {
        if (loadingStage != null && loadingStage.isShowing()) {
            return; // Đã có loading đang hiển thị
        }

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);

        Label label = new Label(message);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        VBox box = new VBox(20, progress, label);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Scene scene = new Scene(root, 300, 200);
        scene.setFill(Color.TRANSPARENT);

        loadingStage.setScene(scene);
        loadingStage.sizeToScene();

        Platform.runLater(() -> {
            if (tableView.getScene() != null && tableView.getScene().getWindow() != null) {
                Stage owner = (Stage) tableView.getScene().getWindow();
                loadingStage.initOwner(owner);
                loadingStage.initModality(Modality.APPLICATION_MODAL);
            }
            loadingStage.show();
        });
    }

    /**
     * Ẩn popup loading
     */
    private void hideLoadingPopup() {
        if (loadingStage != null && loadingStage.isShowing()) {
            loadingStage.close();
            loadingStage = null;
        }
    }

    /**
     * Tạo label placeholder cho TableView
     */
    private Label createPlaceholderLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-text-fill: #666;");
        return label;
    }

    /**
     * Hiển thị alert thông báo
     */
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị alert lỗi
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refresh lại danh sách (public method để gọi từ bên ngoài nếu cần)
     */
    public void refreshData() {
        loadAllGrns();
    }
}
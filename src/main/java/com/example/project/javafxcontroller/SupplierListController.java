package com.example.project.javafxcontroller;

import com.example.project.apiservice.SupplierApiService;
import com.example.project.model.Supplier;
import com.example.project.security.Permission;
import com.example.project.security.UserSession;
import com.example.project.service.SupplierService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SupplierListController {

    @FXML private TableView<Supplier> tableView;
    @FXML private TableColumn<Supplier, String> colSupplierName;
    @FXML private TableColumn<Supplier, String> colContactPerson;
    @FXML private TableColumn<Supplier, String> colPhoneNumber;
    @FXML private TableColumn<Supplier, String> colEmail;
    @FXML private TableColumn<Supplier, String> colAddress;
    @FXML private TableColumn<Supplier, Void> colDetail;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private Button addSupplierButton;

    private SupplierApiService supplierApiService;

    private SupplierService supplierService;

    private Stage loadingStage;

    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private Timeline debounceTimeline;

    private ObservableList<Supplier> masterData = FXCollections.observableArrayList();
    private ObservableList<Supplier> filteredData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        supplierApiService = new SupplierApiService();

        UserSession session = UserSession.getInstance();
        addSupplierButton.setVisible(session.hasPermission(Permission.LIBRARIAN_CREATE));
        setupTableColumns();
        tableView.setItems(supplierList);
        setupComboBox();
        searchLibrarians();
        setupSearch();
    }

    private void setupTableColumns() {
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colContactPerson.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        String cellStyle = "-fx-alignment: CENTER;-fx-font-family: 'Segoe UI Regular'; -fx-font-size: 15px;";
        colSupplierName.setStyle(cellStyle);
        colContactPerson.setStyle(cellStyle);
        colPhoneNumber.setStyle(cellStyle);
        colEmail.setStyle(cellStyle);
        colAddress.setStyle(cellStyle);

//        // Detail Col
//        colDetail.setCellFactory(tc -> new TableCell<>() {
//            private final Button detailBtn = createButton("Xem", "#4CAF50");
//            private final HBox container = new HBox(detailBtn);
//            {
//                container.setAlignment(Pos.CENTER);
//                detailBtn.setOnAction(e -> {
//                    Supplier supplier = getTableView().getItems().get(getIndex());
////                    showDetailDialog(supplier);
//                });
//            }
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
//                    setGraphic(null);
//                } else {
//                    setGraphic(container);
//                }
//            }
//
//            {
//                detailBtn.setOnAction(e -> {
//                    Supplier supplier = getTableView().getItems().get(getIndex());
////                    showDetailDialog(supplier);
//                });
//            }
//        });
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
//        tableView.setRowFactory(tv -> new TableRow<>() {
//            {
//                setPrefHeight(50);
//            }
//        });
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10;"
                , color));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }


    private void searchLibrarians() {
        String keyword = searchField.getText().trim();
        String supplierName = keyword.isEmpty() ? null : keyword;

        String gender = genderComboBox.getValue();
        if ("Tất cả".equals(gender)) gender = null;

        showLoadingPopup("Đang tải danh sách chuyên viên...");

        String finalGender = gender;

        Task<List<Supplier>> task = new Task<>() {
            @Override
            protected List<Supplier> call() throws Exception {
                return supplierApiService.filterSuppliers(supplierName, null);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Supplier> result = task.getValue();
            supplierList.setAll(result != null ? result : List.of());
            tableView.refresh();

            hideLoadingPopup();

            if (result == null || result.isEmpty()) {
                tableView.setPlaceholder(new Label("Không tìm thấy chuyên viên nào."));
            } else {
                tableView.setPlaceholder(new Label(""));
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            hideLoadingPopup();
            Throwable ex = task.getException();
            System.out.println(ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Không kết nối được server!\n" + ex.getMessage());
            alert.show();
            tableView.setPlaceholder(new Label("Lỗi tải dữ liệu..."));
        }));

        new Thread(task).start();
    }


    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldText, newText) -> debounceSearch());
        searchButton.setOnAction(e -> filterTable(searchField.getText()));
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData.setAll(masterData);
        } else {
            String lowerCaseKeyword = keyword.toLowerCase();
            List<Supplier> filtered = masterData.stream()
                    .filter(r ->
                            r.getSupplierName().toLowerCase().contains(lowerCaseKeyword)
                    )
                    .collect(Collectors.toList());
            filteredData.setAll(filtered);
        }
    }

    private void setupComboBox() {
        genderComboBox.setItems(FXCollections.observableArrayList("Tất cả", "Nam", "Nữ"));
        genderComboBox.setOnAction(e -> searchLibrarians());
    }

    private void debounceSearch() {
        if (debounceTimeline != null) debounceTimeline.stop();
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> searchLibrarians()));
        debounceTimeline.play();
    }

//    private void showDetailDialog(Supplier supplier) {
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/com/example/project/supplier_detail_form.fxml"));
//            Parent root = loader.load();
//            SupplierDetailController controller = loader.getController();
//
//            controller.setSupplier(supplier);
//            Stage stage = new Stage();
//            stage.setTitle("Chi tiết chuyên viên");
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setScene(new Scene(root));
//            stage.showAndWait();
//            refreshTable();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

    private void refreshTable() {
        loadApprovalReaders();
        filterTable(searchField.getText());
    }

    private void loadApprovalReaders() {
        List<Supplier> approvedList = supplierService.getSupplier();
        masterData = FXCollections.observableArrayList(approvedList);
        filteredData = FXCollections.observableArrayList(masterData);
        tableView.setItems(filteredData);
    }

    @FXML
    protected void addSupplier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/librarian_add_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm nhà cung cấp");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
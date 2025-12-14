package com.example.project.javafxcontroller;

import com.example.project.apiservice.LibrarianApiService;
import com.example.project.model.Librarian;
import com.example.project.security.Permission;
import com.example.project.security.UserSession;
import com.example.project.service.LibrarianService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LibrarianListController {

    @FXML private TableView<Librarian> tableView;
    @FXML private TableColumn<Librarian, String> colName;
    @FXML private TableColumn<Librarian, String> colMSSV;
    @FXML private TableColumn<Librarian, LocalDate> colDOB;
    @FXML private TableColumn<Librarian, String> colCCCD;
    @FXML private TableColumn<Librarian, String> colMajor;
    @FXML private TableColumn<Librarian, String> colWorkplace;
    @FXML private TableColumn<Librarian, Void> colDetail;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private Button addLibrarianButton;

    private LibrarianApiService librarianApiService;

    private LibrarianService librarianService;

    private Stage loadingStage;

    private final ObservableList<Librarian> librarianList = FXCollections.observableArrayList();
    private Timeline debounceTimeline;

    private ObservableList<Librarian> masterData = FXCollections.observableArrayList();
    private ObservableList<Librarian> filteredData = FXCollections.observableArrayList();

    private final FXMLLoader fxmlLoader = new FXMLLoader();

    @FXML
    public void initialize() {
        librarianApiService = new LibrarianApiService();
        librarianService = new LibrarianService();

        UserSession session = UserSession.getInstance();
        addLibrarianButton.setVisible(session.hasPermission(Permission.LIBRARIAN_CREATE));
        setupTableColumns();
        tableView.setItems(librarianList);
        setupComboBox();
        searchLibrarians();
        setupSearch();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colMSSV.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("idCardNumber"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colWorkplace.setCellValueFactory(new PropertyValueFactory<>("workPlace"));

        String cellStyle = "-fx-alignment: CENTER;-fx-font-family: 'Segoe UI Regular'; -fx-font-size: 15px;";
        colName.setStyle(cellStyle);
        colMSSV.setStyle(cellStyle);
        colDOB.setStyle(cellStyle);
        colCCCD.setStyle(cellStyle);
        colMajor.setStyle(cellStyle);
        colWorkplace.setStyle(cellStyle);

        // Detail Col
        colDetail.setCellFactory(tc -> new TableCell<>() {
            private final Button detailBtn = createButton("Xem", "#4CAF50");
            private final HBox container = new HBox(detailBtn);
            {
                container.setAlignment(Pos.CENTER);
                detailBtn.setOnAction(e -> {
                    Librarian librarian = getTableView().getItems().get(getIndex());
                    showDetailDialog(librarian);
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
                    Librarian librarian = getTableView().getItems().get(getIndex());
                    showDetailDialog(librarian);
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


    private void searchLibrarians() {
        String keyword = searchField.getText().trim();
        String fullName = keyword.isEmpty() ? null : keyword;

        String gender = genderComboBox.getValue();
        if ("Tất cả".equals(gender)) gender = null;

        showLoadingPopup("Đang tải danh sách chuyên viên...");

        String finalGender = gender;

        Task<List<Librarian>> task = new Task<>() {
            @Override
            protected List<Librarian> call() throws Exception {
                return librarianApiService.filterLibrarians(fullName, null, "APPROVED", finalGender);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Librarian> result = task.getValue();
            librarianList.setAll(result != null ? result : List.of());
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
            List<Librarian> filtered = masterData.stream()
                    .filter(r ->
                            r.getFullName().toLowerCase().contains(lowerCaseKeyword)
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

    private void showDetailDialog(Librarian librarian) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/project/librarian_detail_form.fxml"));
            Parent root = loader.load();
            LibrarianDetailController controller = loader.getController();

            controller.setLibrarian(librarian);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết chuyên viên");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void refreshTable() {
        loadApprovalReaders();
        filterTable(searchField.getText());
    }

    private void loadApprovalReaders() {
        List<Librarian> approvedList = librarianService.getApprovedLibrarians();
        masterData = FXCollections.observableArrayList(approvedList);
        filteredData = FXCollections.observableArrayList(masterData);
        tableView.setItems(filteredData);
    }

    @FXML
    protected void addLibrarian(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/librarian_add_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm chuyên viên");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
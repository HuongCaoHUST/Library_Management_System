package com.example.project.javafxcontroller;
import com.example.project.model.Reader;
import com.example.project.apiservice.ReaderApiService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ReaderListController implements Initializable {

    @FXML private TableView<Reader> tableView;
    @FXML private TableColumn<Reader, String> colName;
    @FXML private TableColumn<Reader, String> colMSSV;
    @FXML private TableColumn<Reader, LocalDate> colDOB;
    @FXML private TableColumn<Reader, String> colCCCD;
    @FXML private TableColumn<Reader, String> colMajor;
    @FXML private TableColumn<Reader, String> colWorkplace;
    @FXML private TableColumn<Reader, Void> colDetail;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> genderComboBox;

    @Autowired
    private SpringFxmlLoader fxmlLoader;

    @Autowired
    private ReaderApiService readerApiService;
    private Stage loadingStage;

    private final ObservableList<Reader> readerList = FXCollections.observableArrayList();
    private Timeline debounceTimeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        tableView.setItems(readerList);
        setupComboBox();
        searchReaders();
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
                    Reader reader = getTableView().getItems().get(getIndex());
                    showDetailDialog(reader);
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
                    Reader reader = getTableView().getItems().get(getIndex());
                    showDetailDialog(reader);
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


    private void searchReaders() {
        String keyword = searchField.getText().trim();
        String fullName = keyword.isEmpty() ? null : keyword;

        String gender = genderComboBox.getValue();
        if ("Tất cả".equals(gender)) gender = null;

        showLoadingPopup("Đang tải danh sách bạn đọc...");

        String finalGender = gender;

        Task<List<Reader>> task = new Task<>() {
            @Override
            protected List<Reader> call() throws Exception {
                return readerApiService.filterReaders(fullName, null, "APPROVED", finalGender);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Reader> result = task.getValue();
            readerList.setAll(result != null ? result : List.of());
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
        searchButton.setOnAction(e -> searchReaders());
    }

    private void setupComboBox() {
        genderComboBox.setItems(FXCollections.observableArrayList("Tất cả", "Male", "Female"));
        genderComboBox.setOnAction(e -> searchReaders());
    }

    private void debounceSearch() {
        if (debounceTimeline != null) debounceTimeline.stop();
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> searchReaders()));
        debounceTimeline.play();
    }

    private void showDetailDialog(Reader reader) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/reader_detail_form.fxml");
            Stage stage = new Stage();
            ReaderDetailController controller = (ReaderDetailController) root.getUserData();
            controller.setReader(reader);
            stage.setTitle("Chi tiết bạn đọc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
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
}
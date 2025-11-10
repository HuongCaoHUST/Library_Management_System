package com.example.project.controller;
import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import com.example.project.util.SendEmail;
import com.example.project.util.SessionManager;
import com.example.project.util.SpringFxmlLoader;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Component
public class ReaderApprovalController implements Initializable {

    @FXML private TableView<Reader> tableView;
    @FXML private TableColumn<Reader, String> colName;
    @FXML private TableColumn<Reader, String> colMSSV;
    @FXML private TableColumn<Reader, LocalDate> colDOB;
    @FXML private TableColumn<Reader, String> colCCCD;
    @FXML private TableColumn<Reader, String> colMajor;
    @FXML private TableColumn<Reader, String> colWorkplace;
    @FXML private TableColumn<Reader, Void> colDetail;
    @FXML private TableColumn<Reader, Void> colApprove;
    @FXML private TableColumn<Reader, Void> colReject;

    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @Autowired
    private ReaderService readerService;
    @Autowired
    private SendEmail sendEmail;
    @Autowired
    private SpringFxmlLoader fxmlLoader;

    private ObservableList<Reader> masterData;
    private ObservableList<Reader> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadPendingReaders();
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

        // Approval Col
        colApprove.setCellFactory(tc -> new TableCell<>() {
            private final Button approveBtn = createButton("Duyệt", "#1f3368");
            private final HBox container = new HBox(approveBtn);
            {
                container.setAlignment(Pos.CENTER);
                approveBtn.setOnAction(e -> {
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
                approveBtn.setOnAction(e -> {
                    Reader reader = getTableView().getItems().get(getIndex());
                    approveReader(reader);
                });
            }
        });

        // Reject Col
        colReject.setCellFactory(tc -> new TableCell<>() {
            private final Button rejectBtn = createButton("Từ chối", "#a81c29");
            private final HBox container = new HBox(rejectBtn);
            {
                container.setAlignment(Pos.CENTER);
                rejectBtn.setOnAction(e -> {
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
                rejectBtn.setOnAction(e -> {
                    Reader reader = getTableView().getItems().get(getIndex());
                    rejectReader(reader);
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

    private void loadPendingReaders() {
        List<Reader> pendingList = readerService.getPendingReaders();
        masterData = FXCollections.observableArrayList(pendingList);
        filteredData = FXCollections.observableArrayList(masterData);
        tableView.setItems(filteredData);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldText, newText) -> filterTable(newText));
        searchButton.setOnAction(e -> filterTable(searchField.getText()));
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData.setAll(masterData);
        } else {
            String lowerCaseKeyword = keyword.toLowerCase();
            List<Reader> filtered = masterData.stream()
                    .filter(r ->
                            r.getFullName().toLowerCase().contains(lowerCaseKeyword)
                    )
                    .collect(Collectors.toList());
            filteredData.setAll(filtered);
        }
    }

    private void showDetailDialog(Reader reader) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/reader_approval_detail_form.fxml");
            Stage stage = new Stage();
            ReaderApprovalDetailController controller = (ReaderApprovalDetailController) root.getUserData();
            controller.setReader(reader);
            stage.setTitle("Chi tiết bạn đọc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void approveReader(Reader reader) {
        if (!confirmAction("Phê duyệt", "Bạn có chắc muốn phê duyệt reader này?")) return;

        Stage owner = (Stage) tableView.getScene().getWindow();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label("Đang gửi email thông báo...");
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white;");

        Stage waitStage = new Stage();
        waitStage.initOwner(owner);
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.initStyle(StageStyle.UNDECORATED);
        waitStage.setScene(new Scene(box));
        waitStage.sizeToScene();
        waitStage.show();
        waitStage.getScene().getRoot().requestFocus();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String rawPassword = generateRandomPassword(8);
                reader.setPassword(rawPassword);
                reader.setStatus("APPROVED");
                reader.setApprovedDate(LocalDateTime.now());
                Librarian librarian = SessionManager.getCurrentLibrarian();
                reader.setApprovedBy(librarian);

                String subject = "Tài khoản thư viện của bạn đã được phê duyệt";
                String body = "Xin chào " + reader.getFullName() + ",\n\n"
                        + "Tài khoản của bạn đã được phê duyệt thành công!\n"
                        + "Tên đăng nhập: " + reader.getUsername() + "\n"
                        + "Mật khẩu: " + rawPassword + "\n\n"
                        + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n"
                        + "Thân mến,\nPhòng Thư viện";

                sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
                readerService.save(reader);

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            waitStage.close();
            refreshTable();
            showInfo("Đã phê duyệt thành công!");
        });

        task.setOnFailed(e -> {
            waitStage.close();
        });

        new Thread(task).start();
    }


    private void rejectReader(Reader reader) {
        if (confirmAction("Từ chối", "Bạn có chắc muốn từ chối reader này?")) {
            readerService.delete(reader.getUserId());
            refreshTable();
            showInfo("Đã từ chối!");
        }
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void refreshTable() {
        loadPendingReaders();
        filterTable(searchField.getText());
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
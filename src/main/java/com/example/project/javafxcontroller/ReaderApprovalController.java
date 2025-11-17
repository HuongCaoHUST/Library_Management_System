package com.example.project.javafxcontroller;
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
import java.util.*;
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
    @FXML private TableColumn<Reader, Void> colSelect;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private Button approveSelectedBtn;
    @FXML private Button rejectSelectedBtn;

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
    private final Set<Long> selectedIds = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadPendingReaders();
        setupSelectAll();
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

        // Select Col
        colSelect.setCellFactory(tc -> new TableCell<Reader, Void>() {
            private final CheckBox cb = new CheckBox();
            private final HBox box = new HBox(10, cb);
            {
                box.setAlignment(Pos.CENTER);
                cb.setOnAction(e -> {
                    Reader reader = getTableView().getItems().get(getIndex());
                    Long id = reader.getUserId();
                    if (cb.isSelected()) selectedIds.add(id);
                    else selectedIds.remove(id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Reader r = getTableRow().getItem();
                    cb.setSelected(selectedIds.contains(r.getUserId()));
                    setGraphic(box);
                }
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
                    readerService.approveReader(
                            reader,
                            () -> {
                                refreshTable();
                                showInfo("Đã phê duyệt thành công!");
                            },
                            () -> showError("Lỗi khi phê duyệt!")
                    );
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
                    readerService.rejectReader(
                            reader,
                            () -> {
                                refreshTable();
                                showInfo("Đã từ chối thành công!");
                            },
                            () -> showError("Lỗi khi từ chối!")
                    );
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshTable() {
        selectedIds.clear();
        loadPendingReaders();
        filterTable(searchField.getText());
    }

    private void setupSelectAll() {
        selectAllCheckBox.setOnAction(e -> {
            if (selectAllCheckBox.isSelected()) {
                filteredData.forEach(r -> selectedIds.add(r.getUserId()));
            } else {
                selectedIds.removeIf(id -> filteredData.stream().anyMatch(r -> r.getUserId().equals(id)));
            }
            tableView.refresh(); // Cập nhật checkbox
        });
    }
//
//    @FXML
//    private void handleApproveSelected() {
//        if (selectedIds.isEmpty()) {
//            showInfo("Chưa chọn bạn đọc nào!");
//            return;
//        }
//
//        if (!confirmAction("Phê duyệt", "Phê duyệt " + selectedIds.size() + " bạn đọc?")) return;
//
//        approveMultipleByIds(new ArrayList<>(selectedIds));
//    }

//    private void approveMultipleByIds(List<Long> userIds) {
//        Task<Void> task = new Task<>() {
//            @Override protected Void call() throws Exception {
//                Librarian librarian = SessionManager.getCurrentLibrarian();
//                LocalDateTime now = LocalDateTime.now();
//
//                for (Long id : userIds) {
//                    Reader reader = readerService.findById(id);
//                    if (reader == null) continue;
//
//                    String rawPassword = generateRandomPassword(8);
//                    reader.setPassword(rawPassword);
//                    reader.setStatus("APPROVED");
//                    reader.setApprovedDate(now);
//                    reader.setApprovedBy(librarian);
//                    readerService.save(reader);
//
//                    String body = """
//                    Xin chào %s,
//                    Tài khoản đã được phê duyệt!
//                    Username: %s
//                    Mật khẩu: %s
//                    Vui lòng đổi mật khẩu lần đầu.
//                    """.formatted(reader.getFullName(), reader.getUsername(), rawPassword);
//
//                    sendEmail.sendMail("huongcao.seee@gmail.com", "Tài khoản được duyệt", body);
//                }
//                return null;
//            }
//        };
//
//        showProgressAndRun(task, "Đang phê duyệt...", () -> {
//            selectedIds.clear();
//            refreshTable();
//            showInfo("Đã phê duyệt " + userIds.size() + " bạn đọc!");
//        });
//    }
//
//    @FXML
//    private void handleRejectSelected() {
//        if (selectedIds.isEmpty()) {
//            showInfo("Chưa chọn bạn đọc nào!");
//            return;
//        }
//
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Từ chối hàng loạt");
//        dialog.setContentText("Lý do từ chối:");
//        Optional<String> result = dialog.showAndWait();
//        String reason = result.orElse("Không có lý do");
//
//        rejectMultipleByIds(new ArrayList<>(selectedIds), reason);
//    }
//
//    private void rejectMultipleByIds(List<Long> userIds, String reason) {
//        Task<Void> task = new Task<>() {
//            @Override protected Void call() throws Exception {
//                for (Long id : userIds) {
//                    Reader reader = readerService.findById(id);
//                    if (reader == null) continue;
//
//                    String body = """
//                    Xin chào %s,
//                    Tài khoản bị từ chối.
//                    Lý do: %s
//                    Liên hệ thư viện để biết thêm.
//                    """.formatted(reader.getFullName(), reason);
//
//                    sendEmail.sendMail("huongcao.seee@gmail.com", "Tài khoản bị từ chối", body);
//                    readerService.delete(id);
//                }
//                return null;
//            }
//        };
//
//        showProgressAndRun(task, "Đang từ chối...", () -> {
//            selectedIds.clear();
//            refreshTable();
//            showInfo("Đã từ chối " + userIds.size() + " bạn đọc!");
//        });
//    }


}
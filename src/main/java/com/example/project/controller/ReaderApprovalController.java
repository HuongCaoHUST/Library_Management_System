package com.example.project.controller;
import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import com.example.project.util.SendEmail;
import com.example.project.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private ObservableList<Reader> masterData; // Dữ liệu gốc (PENDING)
    private ObservableList<Reader> filteredData; // Dữ liệu sau tìm kiếm

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
        colWorkplace.setCellValueFactory(new PropertyValueFactory<>("workPlace"));

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
            private final Button approveBtn = createButton("Duyệt", "#2196F3");
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
            private final Button rejectBtn = createButton("Từ chối", "#f44336");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết Reader");
        alert.setHeaderText(reader.getFullName() + " - " + reader.getUserId());

        String content = String.format(
                "Họ và tên: %s\n" +
                        "MSSV: %s\n" +
                        "Ngày sinh: %s\n" +
                        "CCCD: %s\n" +
                        "Nơi công tác: %s\n" +
                        "Trạng thái: %s",
                reader.getFullName(),
                reader.getUserId(),
                reader.getBirthDate(),
                reader.getIdCardNumber(),
                reader.getWorkPlace(),
                reader.getStatus()
        );

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void approveReader(Reader reader) {
        if (confirmAction("Phê duyệt", "Bạn có chắc muốn phê duyệt reader này?")) {
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
            refreshTable();
            showInfo("Đã phê duyệt thành công!");
        }
    }

    private void rejectReader(Reader reader) {
        if (confirmAction("Từ chối", "Bạn có chắc muốn từ chối reader này?")) {
//            reader.setStatus("REJECTED");
//            readerService.save(reader);
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
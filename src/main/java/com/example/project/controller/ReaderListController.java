package com.example.project.controller;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
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
public class ReaderListController implements Initializable {

    @FXML private TableView<Reader> tableView;
    @FXML private TableColumn<Reader, String> colName;
    @FXML private TableColumn<Reader, String> colMSSV;
    @FXML private TableColumn<Reader, LocalDate> colDOB;
    @FXML private TableColumn<Reader, String> colCCCD;
    @FXML private TableColumn<Reader, String> colWorkplace;
    @FXML private TableColumn<Reader, Void> colDetail;

    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @Autowired
    private ReaderService readerService;

    private ObservableList<Reader> masterData; // Dữ liệu gốc (PENDING)
    private ObservableList<Reader> filteredData; // Dữ liệu sau tìm kiếm

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadApprovedReaders();
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

    private void loadApprovedReaders() {
        List<Reader> approvedList = readerService.getApprovedReaders();
        masterData = FXCollections.observableArrayList(approvedList);
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
                        "Trạng thái: %s\n" +
                        "Ngày duyệt: %s\n" +
                        "Người duyệt: %s",
                reader.getFullName(),
                reader.getUserId(),
                reader.getBirthDate(),
                reader.getIdCardNumber(),
                reader.getWorkPlace(),
                reader.getStatus(),
                reader.getApprovedDate(),
                reader.getApprovedBy().getFullName()
        );

        alert.setContentText(content);
        alert.showAndWait();
    }
}
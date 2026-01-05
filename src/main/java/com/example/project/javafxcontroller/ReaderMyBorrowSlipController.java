package com.example.project.javafxcontroller;

import com.example.project.apiservice.BorrowSlipApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.BorrowSlipResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class ReaderMyBorrowSlipController {

    @FXML
    private TableView<BorrowSlipResponse> borrowSlipTable;
    @FXML
    private TableColumn<BorrowSlipResponse, Long> colBorrowSlipId;
    @FXML
    private TableColumn<BorrowSlipResponse, LocalDate> colBorrowDate;
    @FXML
    private TableColumn<BorrowSlipResponse, LocalDate> colDueDate;
    @FXML
    private TableColumn<BorrowSlipResponse, String> colStatus;

    private final BorrowSlipApiService borrowSlipApiService = new BorrowSlipApiService();
    private final ObservableList<BorrowSlipResponse> borrowSlipList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the columns
        colBorrowSlipId.setCellValueFactory(new PropertyValueFactory<>("borrowSlipId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        borrowSlipTable.setItems(borrowSlipList);

        loadBorrowSlips();
    }

    private void loadBorrowSlips() {
        try {
            ApiResponse<List<BorrowSlipResponse>> response = borrowSlipApiService.getMyBorrowSlips();
            if (response.isSuccess()) {
                borrowSlipList.setAll(response.getData());
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách phiếu mượn: " + response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi tải danh sách phiếu mượn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

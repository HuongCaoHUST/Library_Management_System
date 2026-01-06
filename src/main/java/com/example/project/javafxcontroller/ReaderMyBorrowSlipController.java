package com.example.project.javafxcontroller;

import com.example.project.apiservice.BorrowSlipApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.BorrowSlipDetailResponse;
import com.example.project.dto.BorrowSlipResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
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
    @FXML
    private TableColumn<BorrowSlipResponse, Void> colDetail;

    private final BorrowSlipApiService borrowSlipApiService = new BorrowSlipApiService();
    private final ObservableList<BorrowSlipResponse> borrowSlipList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colBorrowSlipId.setCellValueFactory(new PropertyValueFactory<>("borrowSlipId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        addDetailButtonToTable();

        borrowSlipTable.setItems(borrowSlipList);

        loadBorrowSlips();
    }

    private void addDetailButtonToTable() {
        Callback<TableColumn<BorrowSlipResponse, Void>, TableCell<BorrowSlipResponse, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<BorrowSlipResponse, Void> call(final TableColumn<BorrowSlipResponse, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Xem");

                    {
                        btn.getStyleClass().add("detail-button");
                        btn.setOnAction((ActionEvent event) -> {
                            BorrowSlipResponse data = getTableView().getItems().get(getIndex());
                            showDetailView(data.getDetails());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colDetail.setCellFactory(cellFactory);
    }

    private void showDetailView(List<BorrowSlipDetailResponse> details) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/borrow_slip_detail.fxml"));
            Parent root = loader.load();

            BorrowSlipDetailController controller = loader.getController();
            controller.setBorrowSlipDetails(details);

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Phiếu Mượn");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở cửa sổ chi tiết.");
        }
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

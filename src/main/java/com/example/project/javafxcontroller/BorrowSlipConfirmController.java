package com.example.project.javafxcontroller;

import com.example.project.apiservice.BorrowSlipApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.BorrowSlipResponse;
import com.example.project.dto.request.*;
import com.example.project.model.BorrowItem;
import com.example.project.model.Document;
import com.example.project.security.UserSession;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class BorrowSlipConfirmController {

    @FXML private TableView<BorrowItem> confirmTableView;
    @FXML private TableColumn<BorrowItem, Integer> colIndex;
    @FXML private TableColumn<BorrowItem, Long> colDocumentId;
    @FXML private TableColumn<BorrowItem, String> colTitle;
    @FXML private TableColumn<BorrowItem, String> colAuthor;
    @FXML private TableColumn<BorrowItem, Integer> colQuantity;

    private ObservableList<BorrowItem> cartItems;
    private Runnable onSuccessCallback;

    public void setOnSuccessCallback(Runnable onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    @FXML
    public void initialize() {
        colIndex.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        confirmTableView.getItems().indexOf(cellData.getValue()) + 1
                )
        );

        // Mã tài liệu
        colDocumentId.setCellValueFactory(cellData -> {
            Document doc = cellData.getValue().getDocument();
            return new ReadOnlyObjectWrapper<>(doc.getDocumentId());
        });

        // Tên tài liệu
        colTitle.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getDocument().getTitle()
                )
        );

        // Tác giả
        colAuthor.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getDocument().getAuthor()
                )
        );

        // Số lượng (IntegerProperty)
        colQuantity.setCellValueFactory(cellData ->
                cellData.getValue().quantityProperty().asObject()
        );
    }

    public void setCartItems(ObservableList<BorrowItem> cartItems) {
        this.cartItems = cartItems;
        confirmTableView.setItems(cartItems);
    }

    @FXML
    private void handleConfirmBorrow() {
        BorrowSlipRequest dto = buildBorrowSlipDto();
        BorrowSlipApiService api = new BorrowSlipApiService();
        try {
            ApiResponse<BorrowSlipResponse> response = api.createBorrowSlip(dto);

            if (response.isSuccess()) {
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                showAlert(Alert.AlertType.INFORMATION, "Thành công", response.getMessage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Không thành công", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối tới server"
            );
        } finally {
            closeStage();
        }
    }

    private BorrowSlipRequest buildBorrowSlipDto() {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ mượn đang trống");
        }

        BorrowSlipRequest dto = new BorrowSlipRequest();

        Long readerId = UserSession.getInstance().getUserId();
        dto.setReaderId(readerId);
        LocalDate dueDate = LocalDate.now().plusYears(1);
        dto.setDueDate(dueDate);

        List<BorrowSlipDetailRequest> details = cartItems.stream()
                .map(item -> {
                    BorrowSlipDetailRequest detail = new BorrowSlipDetailRequest();
                    detail.setDocumentId(item.getDocument().getDocumentId());
                    detail.setQuantity(item.getQuantity());
                    return detail;
                })
                .toList();

        dto.setDetails(details);
        return dto;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) confirmTableView.getScene().getWindow();
        stage.close();
    }
}

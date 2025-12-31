package com.example.project.javafxcontroller;

import com.example.project.model.BorrowItem;
import com.example.project.model.Document;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class BorrowSlipConfirmController {

    @FXML
    private TableView<BorrowItem> confirmTableView;

    @FXML
    private TableColumn<BorrowItem, Integer> colIndex;

    @FXML
    private TableColumn<BorrowItem, Long> colDocumentId;

    @FXML
    private TableColumn<BorrowItem, String> colTitle;

    @FXML
    private TableColumn<BorrowItem, String> colAuthor;

    @FXML
    private TableColumn<BorrowItem, Integer> colQuantity;

    private ObservableList<BorrowItem> cartItems;

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

    /** 👉 Nhận dữ liệu từ màn hình trước */
    public void setCartItems(ObservableList<BorrowItem> cartItems) {
        this.cartItems = cartItems;
        confirmTableView.setItems(cartItems);
    }

    @FXML
    private void handleConfirmBorrow() {
        // TODO: gọi API tạo borrow slip
        System.out.println("Xác nhận mượn: " + cartItems.size() + " tài liệu");

        closeStage();
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

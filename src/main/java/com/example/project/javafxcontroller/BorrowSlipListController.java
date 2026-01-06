package com.example.project.javafxcontroller;

import com.example.project.apiservice.BorrowSlipApiService;
import com.example.project.dto.BorrowSlipResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class BorrowSlipListController {

    @FXML
    private AnchorPane rootPane;
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
        // Detail column will be added later if needed

        borrowSlipTable.setItems(borrowSlipList);

        // Data loading logic will be added later
    }
}

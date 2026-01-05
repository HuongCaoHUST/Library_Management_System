package com.example.project.javafxcontroller;

import com.example.project.dto.BorrowSlipDetailResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class BorrowSlipDetailController {

    @FXML
    private TableView<BorrowSlipDetailResponse> detailsTable;

    @FXML
    private TableColumn<BorrowSlipDetailResponse, Long> colDocumentId;

    @FXML
    private TableColumn<BorrowSlipDetailResponse, String> colDocumentTitle;

    @FXML
    private TableColumn<BorrowSlipDetailResponse, Integer> colQuantity;

    private final ObservableList<BorrowSlipDetailResponse> detailList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        colDocumentTitle.setCellValueFactory(new PropertyValueFactory<>("documentTitle"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        detailsTable.setItems(detailList);
    }

    public void setBorrowSlipDetails(List<BorrowSlipDetailResponse> details) {
        detailList.setAll(details);
    }
}

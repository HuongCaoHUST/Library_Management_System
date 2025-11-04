package com.example.project.controller;

import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class ReaderController {

    @FXML
    private TableView<Reader> readerTable;
    @FXML
    private TableColumn<Reader, String> colFullName;
    @FXML
    private TableColumn<Reader, String> colEmail;
    @FXML
    private TableColumn<Reader, String> colPhone;

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @FXML
    public void initialize() {
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        readerTable.getItems().setAll(readerService.findAll());
    }
}

package com.example.project.javafxcontroller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RoleListController {

    @FXML
    private TableView<String> roleTable;

    @FXML
    private TableColumn<String, String> roleNameCol;

    @FXML
    public void initialize() {
        roleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        ObservableList<String> roles = FXCollections.observableArrayList(
                "ADMIN",
                "USER",
                "GUEST"
        );
        roleTable.setItems(roles);
    }
}


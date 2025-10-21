package com.example.project.controller;

import com.example.project.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AccountInforController extends HomeControllerForAdmin {

    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colMSSV;
    @FXML
    private TableColumn<User, String> colDOB;
    @FXML
    private TableColumn<User, String> colCCCD;
    @FXML
    private TableColumn<User, String> colWorkplace;
    @FXML
    private TableColumn<User, String> colDetail;
    @FXML
    private TextField searchField;
    @FXML
    private AnchorPane mainContent;

    @FXML
    public void initialize() {
        super.initialize();
    }
    @FXML
    public void handleApproveAccount(ActionEvent event) {
        super.handleApproveAccount(event);
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/home_admin_form.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Trang chủ quản trị viên");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

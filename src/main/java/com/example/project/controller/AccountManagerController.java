package com.example.project.controller;
import com.example.project.model_controller.UserController;
import com.example.project.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AccountManagerController extends HomeControllerForAdmin {

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
    private UserController userController;

    @FXML
    public void initialize() {
        super.initialize();
        userController = new UserController();
    }
    @FXML
    @Override
    protected void handleApproveAccount(ActionEvent event) {
        super.handleApproveAccount(event);
    }

    public void loadAccountList() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        Path path = Paths.get("data/users.txt");

        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 12) {
                        User user = new User(parts);
                        userList.add(user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Gán dữ liệu cho các cột
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colMSSV.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("idCardNumber"));
        colWorkplace.setCellValueFactory(new PropertyValueFactory<>("workPlace"));

        String columnStyle = "-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;";
        colName.setStyle(columnStyle);
        colMSSV.setStyle(columnStyle);
        colDOB.setStyle(columnStyle);
        colCCCD.setStyle(columnStyle);
        colWorkplace.setStyle(columnStyle);

        // Tạo nút "Chi tiết"
        colDetail.setCellFactory(col -> new TableCell<User, String>() {
            private final Button btn = new Button(); {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("/com/example/project/logo/view.png"))
                );
                icon.setFitWidth(24);
                icon.setFitHeight(24);
                btn.setGraphic(icon);
                btn.setStyle("-fx-background-color: transparent; " + "-fx-padding: 0; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDetails(user);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        tableView.setItems(userList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void showUserDetails(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/detail_accoun_mini_form.fxml"));
            AnchorPane root = loader.load();

            User userSelected = loadUserByStudentID(user.getStudentId());
            DetailAccountController controller = loader.getController();
            controller.setUser(userSelected);
            controller.setUserController(userController);
            controller.setParentController(this);

            // Tạo Stage mới
            Stage stage = new Stage();
            stage.setTitle("Chi tiết tài khoản");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User loadUserByStudentID(String studentID) {
        System.out.println(studentID);
        Path path = Paths.get("data/users.txt");
        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 12) {
                        if (parts[0].equals(studentID)) {
                            return new User(parts);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

package com.example.project.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.example.project.models.User;

public class HomeControllerForAdmin {
    @FXML
    private AnchorPane mainContent;
    @FXML
    private Label notificationBadge;

    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colMSSV;
    @FXML private TableColumn<User, String> colDOB;
    @FXML private TableColumn<User, String> colCCCD;
    @FXML private TableColumn<User, String> colWorkplace;
    @FXML private TableColumn<User, String> colDetail;
    @FXML private TableColumn<User, String> colApprove;

    @FXML
    private void handleApproveAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/account_approval_form.fxml"));
            Parent root = loader.load();

            HomeControllerForAdmin controller = loader.getController();
            controller.loadRegisterQueue();
            controller.updateNotificationBadge();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Duyệt tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNotificationBadge() {
        if (notificationBadge == null) return;
        Path filePath = Paths.get("./data/register_queue.txt");
        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                int count = lines.size();

                notificationBadge.setVisible(count > 0);
                if (count > 0) notificationBadge.setText(String.valueOf(count));
            } else {
                notificationBadge.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            notificationBadge.setVisible(false);
        }
    }

    public void loadRegisterQueue() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        Path path = Paths.get("data/register_queue.txt");

        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    // Giả sử file phân tách bằng dấu phẩy
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

        colName.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;");
        colMSSV.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;");
        colDOB.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;");
        colCCCD.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;");
        colWorkplace.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;");

        // Tạo nút "Chi tiết"
        colDetail.setCellFactory(col -> new TableCell<User, String>() {
            private final Button btn = new Button(); {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("/com/example/project/logo/view.png"))
                );
                icon.setFitWidth(24);
                icon.setFitHeight(24);
                btn.setGraphic(icon);
                btn.setStyle("-fx-background-color: transparent; " + "-fx-padding: 0;");
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


        // Tạo nút "Phê duyệt"
        colApprove.setCellFactory(col -> new TableCell<User, String>() {
            private final Button btn = new Button("Phê duyệt");

            {
                btn.setStyle(
                        "-fx-background-color: #1f3368; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; " + "-fx-background-radius: 5;" );
                btn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    approveUser(user);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
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

    // Hiển thị chi tiết người dùng (có thể mở popup hoặc Alert)
    private void showUserDetails(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết người dùng");
        alert.setHeaderText(user.getFullName());
        String content = "MSSV: " + user.getStudentId() + "\n" +
                "Ngày sinh: " + user.getBirthDate() + "\n" +
                "Giới tính: " + user.getGender() + "\n" +
                "Số CCCD: " + user.getIdCardNumber() + "\n" +
                "Nơi công tác: " + user.getWorkPlace() + "\n" +
                "Địa chỉ: " + user.getAddress();
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Xử lý phê duyệt người dùng
    private void approveUser(User user) {
        // Ở đây bạn có thể xoá người dùng khỏi file hoặc đánh dấu đã duyệt
        System.out.println("Đã phê duyệt: " + user.getFullName());
    }
}

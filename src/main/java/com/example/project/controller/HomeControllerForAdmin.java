package com.example.project.controller;
import com.example.project.model_controller.UserController;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private TableColumn<User, String> colReject;
    @FXML private TextField searchField;
    private UserController userController;
    private ObservableList<User> originalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userController = new UserController();
    }

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

        // Tạo nút "Từ chối"
        colReject.setCellFactory(col -> new TableCell<User, String>() {
            private final Button btn = new Button("Từ chối");

            {
                btn.setStyle(
                        "-fx-background-color: #a81c29; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; " + "-fx-background-radius: 5;" );
                btn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    rejectUser(user);
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
        originalList.setAll(userList);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/detail_register_form.fxml"));
            AnchorPane root = loader.load();

            User userSelected = loadUserByStudentID(user.getStudentId());
            DetailRegisterController controller = loader.getController();
            controller.setUser(userSelected);
            controller.setUserController(userController);
            controller.setParentController(this);
            System.out.println("userSelected: " + userSelected);

            // Tạo Stage mới
            Stage stage = new Stage();
            stage.setTitle("Chi tiết tài khoản");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // cửa sổ modal
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User loadUserByStudentID(String studentID) {
        Path path = Paths.get("data/register_queue.txt");
        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);

                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 12) {
                        // Giả sử StudentID là phần đầu tiên (index 0)
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

    // Xử lý phê duyệt người dùng
    private void approveUser(User user) {
        userController.addUser(user);
        removeFromRegisterQueue(user);
        tableView.getItems().remove(user);
        showAlert("Duyệt tài khoản thành công!");
    }

    private void rejectUser(User user) {
        removeFromRegisterQueue(user);
        tableView.getItems().remove(user);
        showAlert("Từ chối tài khoản thành công!");
    }

    private void removeFromRegisterQueue(User user) {
        Path path = Paths.get("data/register_queue.txt");
        try {
            if (!Files.exists(path)) return;

            List<String> lines = Files.readAllLines(path);
            List<String> updated = lines.stream()
                    .filter(line -> !line.startsWith(user.getStudentId() + ","))
                    .collect(Collectors.toList());

            Files.write(path, updated, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xóa khỏi register_queue.txt");
        }
    }

    @FXML
    private void search_account() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            tableView.setItems(originalList);
            return;
        }

        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User user : originalList) {
            if (user.getFullName().toLowerCase().contains(keyword) ||
                    user.getStudentId().toLowerCase().contains(keyword)) {
                filteredList.add(user);
            }
        }

        tableView.setItems(filteredList);
        tableView.refresh();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        alert.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }
}

package com.example.project.controller;
import com.example.project.model_controller.UserController;
import com.example.project.models.User;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HomeControllerForAdmin extends LoadForm {
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

    private UserController userController;

    @FXML
    public void initialize() {
        userController = new UserController();
        super.notificationBadge = this.notificationBadge;
        updateNotificationBadge();
    }
    @FXML
    protected void handleApproveAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/account_approval_form.fxml"));
            Parent root = loader.load();
            ApprovalAccountController controller = loader.getController();
            controller.loadRegisterQueue();
            controller.updateNotificationBadge();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Phê duyệt tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleAccountInfor(ActionEvent event) {
        loadForm(event, "/com/example/project/detail_account_infor_form.fxml", "Thông tin tài khoản");
    }

    protected void showAlert(String message) {
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

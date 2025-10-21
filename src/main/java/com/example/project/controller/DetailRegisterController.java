package com.example.project.controller;

import com.example.project.models.User;
import com.example.project.model_controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class DetailRegisterController {
    @FXML private ImageView imgPhoto;
    @FXML private Label lblFullName;
    @FXML private Label lblStudentId;
    @FXML private Label lblGender;
    @FXML private Label lblBirthDate;
    @FXML private Label lblPhoneNumber;
    @FXML private Label lblEmail;
    @FXML private Label lblPlaceOfBirth;
    @FXML private Label lblAddress;
    @FXML private Label lblIdCardNumber;
    @FXML private Label lblIssuedPlace;
    @FXML private Label lblMajor;
    @FXML private Label lblWorkPlace;
    @FXML private Button btnApprove;
    private User currentUser;
    private UserController userController;

    public void setUser(User user) {
        this.currentUser = user;
        lblFullName.setText(user.getFullName());
        lblStudentId.setText(user.getStudentId());
        lblGender.setText(user.getGender());
        lblBirthDate.setText(user.getBirthDate());
        lblPhoneNumber.setText(user.getPhoneNumber());
        lblEmail.setText(user.getEmail());
        lblPlaceOfBirth.setText(user.getPlaceOfBirth());
        lblAddress.setText(user.getAddress());
        lblIdCardNumber.setText(user.getIdCardNumber());
        lblIssuedPlace.setText(user.getIssuedPlace());
        lblMajor.setText(user.getMajor());
        lblWorkPlace.setText(user.getWorkPlace());
        try {
            Image defaultImage = new Image(new FileInputStream("data/human.png"));
            imgPhoto.setImage(defaultImage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUserController(UserController uc) {
        this.userController = uc;
    }

    private HomeControllerForAdmin parentController;

    public void setParentController(HomeControllerForAdmin parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleApprove() {
        if (currentUser != null && userController != null) {
            System.out.println("Controler: " + userController);
            userController.addUser(currentUser);
            removeFromRegisterQueue(currentUser);
            showAlert("Duyệt tài khoản thành công!");
            if (parentController != null) {
                parentController.loadRegisterQueue();
            }
            Stage stage = (Stage) lblFullName.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleReject() {
        if (currentUser != null) {
            removeFromRegisterQueue(currentUser);
            showAlert("Từ chối tài khoản thành công!");

            if (parentController != null) {
                parentController.loadRegisterQueue();
            }
            Stage stage = (Stage) lblFullName.getScene().getWindow();
            stage.close();
        }
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
            showAlert("Lỗi khi xóa khỏi register_queue.txt");
        }
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

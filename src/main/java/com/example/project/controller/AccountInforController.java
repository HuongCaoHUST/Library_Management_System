package com.example.project.controller;

import com.example.project.model_controller.UserController;
import com.example.project.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AccountInforController extends HomeControllerForAdmin {

    @FXML private Label lblFullName, lblStudentId, lblGender, lblBirthDate, lblPhoneNumber,
            lblEmail, lblPlaceOfBirth, lblAddress, lblIdCardNumber, lblIssuedPlace, lblMajor,
            lblWorkPlace;
    @FXML private ImageView imgPhoto;

    @FXML
    public void initialize() {
        super.initialize();
        loadUserByStudentID("admin");
    }

    private final UserController userController = new UserController();

    public void loadUserByStudentID(String studentID) {
        User user = userController.getUserById(studentID);
        if (user == null) {
            showAlert("Không tìm thấy MSSV: " + studentID);
            return;
        }

        lblStudentId.setText(user.getStudentId());
        lblFullName.setText(user.getFullName());
        lblGender.setText(user.getGender());
        lblBirthDate.setText(user.getBirthDate());
        lblPhoneNumber.setText(user.getPhoneNumber());
        lblEmail.setText(user.getEmail());
        lblIdCardNumber.setText(user.getIdCardNumber());
        lblPlaceOfBirth.setText(user.getPlaceOfBirth());
        lblIssuedPlace.setText(user.getIssuedPlace());
        lblMajor.setText(user.getMajor());
        lblWorkPlace.setText(user.getWorkPlace());
        lblAddress.setText(user.getAddress());
        try {
            Image defaultImage = new Image(new FileInputStream("data/human.png"));
            imgPhoto.setImage(defaultImage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

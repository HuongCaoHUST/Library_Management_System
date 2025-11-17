package com.example.project.javafxcontroller;

import com.example.project.model.Librarian;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class LibrarianDetailController {
    @FXML private Label lblFullName;
    @FXML private Label lblUserId;
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

    public void setLibrarian(Librarian librarian) {
        lblFullName.setText(librarian.getFullName());
        lblUserId.setText(String.valueOf(librarian.getUserId()));
        lblGender.setText(librarian.getGender());
        lblBirthDate.setText(librarian.getBirthDate() != null ? librarian.getBirthDate().toString() : "");
        lblPhoneNumber.setText(librarian.getPhoneNumber());
        lblEmail.setText(librarian.getEmail());
        lblPlaceOfBirth.setText(librarian.getPlaceOfBirth());
        lblAddress.setText(librarian.getAddress());
        lblIdCardNumber.setText(librarian.getIdCardNumber());
        lblIssuedPlace.setText(librarian.getIssuedPlace());
        lblMajor.setText(librarian.getMajor());
        lblWorkPlace.setText(librarian.getWorkPlace());
    }
}
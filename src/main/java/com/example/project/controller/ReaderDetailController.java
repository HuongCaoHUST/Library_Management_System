package com.example.project.controller;
import com.example.project.model.Reader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class ReaderDetailController {
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
    @FXML private Label lblApprovedDate;
    @FXML private Label lblApprovedBy;

    public void setReader(Reader reader) {
        lblFullName.setText(reader.getFullName());
        lblUserId.setText(String.valueOf(reader.getUserId()));
        lblGender.setText(reader.getGender());
        lblBirthDate.setText(reader.getBirthDate() != null ? reader.getBirthDate().toString() : "");
        lblPhoneNumber.setText(reader.getPhoneNumber());
        lblEmail.setText(reader.getEmail());
        lblPlaceOfBirth.setText(reader.getPlaceOfBirth());
        lblAddress.setText(reader.getAddress());
        lblIdCardNumber.setText(reader.getIdCardNumber());
        lblIssuedPlace.setText(reader.getIssuedPlace());
        lblMajor.setText(reader.getMajor());
        lblWorkPlace.setText(reader.getWorkPlace());
        lblApprovedDate.setText(reader.getApprovedDate().toString());
        lblApprovedBy.setText(reader.getApprovedBy().getFullName());
    }
}
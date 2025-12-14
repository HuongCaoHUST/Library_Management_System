package com.example.project.javafxcontroller;

import com.example.project.model.Librarian;
import com.example.project.model.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class SupplierDetailController {
    @FXML private Label lblSupplierName;
    @FXML private Label lblContactPerson;
    @FXML private Label lblPhoneNumber;
    @FXML private Label lblEmail;
    @FXML private Label lblAddress;

    public void setSupplier(Supplier supplier) {
        lblSupplierName.setText(supplier.getSupplierName());
        lblContactPerson.setText(supplier.getContactPerson());
        lblPhoneNumber.setText(supplier.getPhoneNumber());
        lblEmail.setText(supplier.getEmail());
        lblAddress.setText(supplier.getAddress());
    }
}
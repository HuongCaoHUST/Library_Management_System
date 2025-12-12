package com.example.project.javafxcontroller;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

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
    @FXML private Label lblExpirationDate;
    @FXML private Label lblApprovedBy;

    @FXML
    private Button btnReject;

    private Reader currentReader;

    private ReaderService readerService;

    public void setReader(Reader reader) {
        this.currentReader = reader;
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
        lblExpirationDate.setText(reader.getExpirationDate().toString());
//        lblApprovedBy.setText(reader.getApprovedBy().getFullName());
    }

    @FXML
    private void deleteSelectedReader() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa tài khoản này?");
        confirm.setContentText("Người dùng: " + currentReader.getFullName());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
//                readerService.delete(currentReader.getUserId());
                showAlert(Alert.AlertType.INFORMATION, "Đã xóa", "Tài khoản đã được xóa thành công!");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
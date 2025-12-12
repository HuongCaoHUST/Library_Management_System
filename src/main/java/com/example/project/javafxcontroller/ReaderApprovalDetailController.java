package com.example.project.javafxcontroller;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReaderApprovalDetailController {
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

    private Reader currentReader;


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
    }

//    @FXML
//    private void handleApprove() {
//        readerService.approveReader(currentReader,
//                () -> {
//                    showInfo("Đã phê duyệt thành công!");
//                },
//                () -> showError("Lỗi khi phê duyệt!")
//        );
//    }
//
//    @FXML
//    private void handleReject() {
//        readerService.rejectReader(currentReader,
//                () -> {
//                    showInfo("Đã từ chối thành công!");
//                },
//                () -> showError("Lỗi khi từ chối!")
//        );
//    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
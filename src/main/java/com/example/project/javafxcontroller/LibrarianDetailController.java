package com.example.project.javafxcontroller;

import com.example.project.model.Librarian;
import com.example.project.apiservice.LibrarianApiService;
import com.example.project.security.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;

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

    @FXML private Button btnApprove;
    @FXML private Button deleteLibrarianButton;

    private LibrarianApiService librarianApiService;
    private Librarian currentLibrarian;

    @FXML
    public void initialize() {
        librarianApiService = new LibrarianApiService();

        if (UserSession.getInstance().hasPermission("Xóa thủ thư")) {
            deleteLibrarianButton.setVisible(true);
        } else {
            deleteLibrarianButton.setVisible(false);
        }
    }

    public void setLibrarian(Librarian librarian) {
        this.currentLibrarian = librarian;
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

    @FXML
    private void deleteLibrarian(ActionEvent event) {
        if (currentLibrarian == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có thủ thư nào được chọn để xóa.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa thủ thư này?");
        alert.setContentText("Hành động này không thể hoàn tác.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                librarianApiService.deleteLibrarian(currentLibrarian.getUserId());
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thủ thư thành công.");
                ((javafx.stage.Stage) deleteLibrarianButton.getScene().getWindow()).close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa thủ thư: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
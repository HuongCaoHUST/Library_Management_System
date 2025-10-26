package com.example.project.controller;

import com.example.project.models.Reader;
import com.example.project.model_controller.ReaderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Account_Register {
    @FXML private TextField txtFullName;
    @FXML private TextField txtStudentId;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtEmail;
    @FXML private TextField txtIdCardNumber;
    @FXML private TextField txtPlaceOfBirth;
    @FXML private TextField txtIssuedPlace;
    @FXML private TextField txtMajor;
    @FXML private TextField txtWorkPlace;
    @FXML private TextField txtAddress;

    private final ReaderController userController = new ReaderController();


    @FXML
    public void initialize() {
        cbGender.getItems().addAll("Nam", "Nữ");
    }

    @FXML
    private void handleBackToLogin(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/login_form.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String fullName = txtFullName.getText().trim();
        String gender = cbGender.getValue();
        LocalDate birthDate = dpBirthDate.getValue();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String email = txtEmail.getText().trim();
        String idCardNumber = txtIdCardNumber.getText().trim();
        String placeOfBirth = txtPlaceOfBirth.getText().trim();
        String issuedPlace = txtIssuedPlace.getText().trim();
        String major = txtMajor.getText().trim();
        String workPlace = txtWorkPlace.getText().trim();
        String address = txtAddress.getText().trim();
        int maxId = userController.getMaxUserIdFromFile();
        String username = "" + maxId;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();


        // Kiểm tra các trường bắt buộc
        if(fullName.isEmpty() || gender.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ các thông tin bắt buộc!");
            return;
        }

        String birthDateStr = birthDate != null
                ? birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";

        String[] parts = {
                "",
                fullName,
                gender,
                birthDateStr,
                phoneNumber,
                email,
                idCardNumber,
                placeOfBirth,
                issuedPlace,
                major,
                workPlace,
                address,
                "", "", "", "", "", username, "", "", "0", "", ""
        };
        Reader newUser = new Reader(parts);
        newUser.setAccountCreationDate(now);
        addUserToRegisterQueue(newUser);
        showAlert("Đăng ký thành công!");
        clearFields();
    }

    private void addUserToRegisterQueue(Reader reader) {
        Path path = Paths.get("data/register_queue.csv");

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
                String header = "UserID,FullName,Gender,BirthDate,PhoneNumber,Email,IdCardNumber,PlaceOfBirth,IssuedPlace,Major,WorkPlace,Address,AccountCreationDate,AccountApprovalDateAccountant,AccountApproverAccountant,AccountApprovalDateOfficer,AccountApproverOfficer,Username,Password,AccountStatus,PaymentAmount,PaymentTime,CardImage";
                Files.write(path, (header + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            String line = String.join(",",
                    reader.getUserId(),
                    reader.getFullName(),
                    reader.getGender() != null ? reader.getGender().name() : "",
                    reader.getBirthDate() != null ? reader.getBirthDate().format(dateFormatter) : "",
                    reader.getPhoneNumber(),
                    reader.getEmail(),
                    reader.getIdCardNumber(),
                    reader.getPlaceOfBirth(),
                    reader.getIssuedPlace(),
                    reader.getMajor(),
                    reader.getWorkPlace(),
                    reader.getAddress(),
                    reader.getAccountCreationDate() != null ? reader.getAccountCreationDate().format(dateTimeFormatter) : "",
                    reader.getAccountApprovalDateAccountant() != null ? reader.getAccountApprovalDateAccountant().format(dateFormatter) : "",
                    reader.getAccountApproverAccountant(),
                    reader.getAccountApprovalDateOfficer() != null ? reader.getAccountApprovalDateOfficer().format(dateFormatter) : "",
                    reader.getAccountApproverOfficer(),
                    reader.getUsername(),
                    reader.getPassword(),
                    reader.getAccountStatus() != null ? reader.getAccountStatus().name() : "",
                    String.valueOf(reader.getPaymentAmount()),
                    reader.getPaymentTime() != null ? reader.getPaymentTime().format(dateTimeFormatter) : "",
                    reader.getCardImage()
            );
            Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi ghi vào register_queue.csv");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        alert.showAndWait();
    }

    private void clearFields() {
        txtFullName.clear();
        cbGender.getSelectionModel().clearSelection();
        txtPhoneNumber.clear();
        dpBirthDate.setValue(null);
        txtEmail.clear();
        txtIdCardNumber.clear();
        txtPlaceOfBirth.clear();
        txtIssuedPlace.clear();
        txtMajor.clear();
        txtWorkPlace.clear();
        txtAddress.clear();
    }
}
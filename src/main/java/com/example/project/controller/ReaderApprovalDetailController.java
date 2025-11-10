package com.example.project.controller;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import com.example.project.util.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.example.project.util.SendEmail;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
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
    @FXML
    private Button btnApprove;

    private Reader currentReader;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private SendEmail sendEmail;

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

    @FXML
    private void handleApprove() {
        approveReader(currentReader);
    }

    private void approveReader(Reader reader) {
        if (!confirmAction("Phê duyệt", "Bạn có chắc muốn phê duyệt reader này?")) return;

        Stage owner = (Stage) btnApprove.getScene().getWindow();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label("Đang gửi email thông báo...");
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white;");

        Stage waitStage = new Stage();
        waitStage.initOwner(owner);
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.initStyle(StageStyle.UNDECORATED);
        waitStage.setScene(new Scene(box));
        waitStage.sizeToScene();
        waitStage.show();
        waitStage.getScene().getRoot().requestFocus();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String rawPassword = generateRandomPassword(8);
                reader.setPassword(rawPassword);
                reader.setStatus("APPROVED");
                reader.setApprovedDate(LocalDateTime.now());
                Librarian librarian = SessionManager.getCurrentLibrarian();
                reader.setApprovedBy(librarian);

                String subject = "Tài khoản thư viện của bạn đã được phê duyệt";
                String body = "Xin chào " + reader.getFullName() + ",\n\n"
                        + "Tài khoản của bạn đã được phê duyệt thành công!\n"
                        + "Tên đăng nhập: " + reader.getUsername() + "\n"
                        + "Mật khẩu: " + rawPassword + "\n\n"
                        + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n"
                        + "Thân mến,\nPhòng Thư viện";

                sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
                readerService.save(reader);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            waitStage.close();
            showInfo("Đã phê duyệt thành công!");
        });

        task.setOnFailed(e -> {
            waitStage.close();
        });

        new Thread(task).start();
    }

    @FXML
    private void handleReject() {
        rejectReader(currentReader);
    }

    private void rejectReader(Reader reader) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Từ chối tài khoản");
        dialog.setHeaderText(null);
        dialog.setContentText("Vui lòng nhập lý do từ chối tài khoản:");
        Optional<String> result = dialog.showAndWait();

        final String reason = result.isPresent() && !result.get().trim().isEmpty() ? result.get().trim(): " ";
        Stage owner = (Stage) btnApprove.getScene().getWindow();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label("Đang gửi email thông báo...");
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white;");

        Stage waitStage = new Stage();
        waitStage.initOwner(owner);
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.initStyle(StageStyle.UNDECORATED);
        waitStage.setScene(new Scene(box));
        waitStage.sizeToScene();
        waitStage.show();
        waitStage.getScene().getRoot().requestFocus();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String subject = "Tài khoản thư viện của bạn đã bị từ chối";
                String body = "Xin chào " + reader.getFullName() + ",\n\n"
                        + "Tài khoản của bạn đã bị từ chối!\n"
                        + "Lý do từ chối: " + reason + "\n"
                        + "Vui lòng liên hệ Trung tâm thư viện trong giờ hành chính để được giúp đỡ.\n\n"
                        + "Thân mến,\nPhòng Thư viện";

                sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
                readerService.save(reader);

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            waitStage.close();
            readerService.delete(reader.getUserId());
            showInfo("Đã gửi email thông báo tới bạn đọc thành công!");
        });

        task.setOnFailed(e -> {
            waitStage.close();
        });

        new Thread(task).start();
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
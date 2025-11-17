package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.ReaderRepository;
import com.example.project.specification.ReaderSpecification;
import com.example.project.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.example.project.util.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReaderService {
    private final ReaderRepository repository;

    public ReaderService(ReaderRepository repository) {
        this.repository = repository;
    }

    public List<Reader> findAll() {
        return repository.findAll();
    }

    public Optional<Reader> findById(Long id) {
        return repository.findById(id);
    }

    public Reader save(Reader reader) {
        return repository.save(reader);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private SendEmail sendEmail;

    public Reader registerReader(Reader inputReader) {
        String email = inputReader.getEmail().trim().toLowerCase();
        if (repository.findAll().stream().anyMatch(r -> r.getEmail().equalsIgnoreCase(inputReader.getEmail()))) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        if (repository.findAll().stream().anyMatch(r -> r.getIdCardNumber().equals(inputReader.getIdCardNumber()))) {
            throw new IllegalArgumentException("Số CCCD đã được đăng ký!");
        }

        String username = email;
        Reader reader = Reader.builder()
                .fullName(inputReader.getFullName())
                .gender(inputReader.getGender())
                .birthDate(inputReader.getBirthDate())
                .phoneNumber(inputReader.getPhoneNumber())
                .email(inputReader.getEmail())
                .idCardNumber(inputReader.getIdCardNumber())
                .placeOfBirth(inputReader.getPlaceOfBirth())
                .issuedPlace(inputReader.getIssuedPlace())
                .major(inputReader.getMajor())
                .workPlace(inputReader.getWorkPlace())
                .address(inputReader.getAddress())
                .username(username)
                .password(null)
                .registrationDate(LocalDateTime.now())
                .status("PENDING")
                .depositAmount(BigDecimal.ZERO)
                .build();

        return repository.save(reader);
    }

    public void approveReader(Reader reader, Runnable onSuccess, Runnable onFailure) {
        if (!confirmAction("Phê duyệt", "Bạn có chắc muốn phê duyệt reader này?")) return;
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label("Đang gửi email thông báo...");
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white;");

        Stage waitStage = new Stage();
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
                save(reader);
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            waitStage.close();
            if (onSuccess != null) onSuccess.run();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            waitStage.close();
            if (onFailure != null) onFailure.run();
        }));
        new Thread(task).start();
    }

    public void rejectReader(Reader reader, Runnable onSuccess, Runnable onFailure) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Từ chối tài khoản");
        dialog.setHeaderText(null);
        dialog.setContentText("Vui lòng nhập lý do từ chối tài khoản:");
        Optional<String> result = dialog.showAndWait();

        final String reason = result.isPresent() && !result.get().trim().isEmpty() ? result.get().trim(): " ";

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);
        Label label = new Label("Đang gửi email thông báo...");
        VBox box = new VBox(20, label, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white;");

        Stage waitStage = new Stage();
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
                delete(reader.getUserId());
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            waitStage.close();
            if (onSuccess != null) onSuccess.run();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            waitStage.close();
            if (onFailure != null) onFailure.run();
        }));
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

    public List<Reader> getPendingReaders() {
        return readerRepository.findByStatus("PENDING");
    }

    public List<Reader> getApprovedReaders() {
        return readerRepository.findByStatus("APPROVED");
    }
    public List<Reader> filterReaders(String fullName, String email, String status, String gender) {
        Specification<Reader> spec = Specification
                .where(ReaderSpecification.hasFullName(fullName))
                .and(ReaderSpecification.hasEmail(email))
                .and(ReaderSpecification.hasStatus(status))
                .and(ReaderSpecification.hasGender(gender));

        return repository.findAll(spec);
    }
}

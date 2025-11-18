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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void approveMultipleByIds(List<Long> selectedIds, Runnable onSuccess, Runnable onFailure) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,"Lỗi" ,"Không có reader nào được chọn để phê duyệt.");
            return;
        }

        if (!confirmAction("Phê duyệt nhiều reader",
                "Bạn có chắc muốn phê duyệt " + selectedIds.size() + " reader đã chọn?")) {
            return;
        }

        // Dialog chờ với tiến độ chi tiết
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);

        Label statusLabel = new Label("Chuẩn bị phê duyệt...");
        Label detailLabel = new Label("0 / " + selectedIds.size());

        VBox box = new VBox(15, new Label("Đang phê duyệt hàng loạt..."), statusLabel, detailLabel, progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white; -fx-border-color: #dddddd; -fx-border-width: 1;");

        Stage waitStage = new Stage();
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.initStyle(StageStyle.UNDECORATED);
        waitStage.setScene(new Scene(box));
        waitStage.sizeToScene();
        waitStage.show();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Librarian currentLibrarian = SessionManager.getCurrentLibrarian();
                int total = selectedIds.size();

                for (int i = 0; i < total; i++) {
                    if (isCancelled()) break;

                    Long readerId = selectedIds.get(i);
                    Platform.runLater(() -> {
                        statusLabel.setText("Đang xử lý reader ID: " + readerId);
                        detailLabel.setText(processed.incrementAndGet() + " / " + total);
                    });

                    try {
                        Reader reader = readerRepository.findById(readerId).orElse(null);
                        if (reader == null || "APPROVED".equals(reader.getStatus())) {
                            failedCount.incrementAndGet();
                            continue;
                        }

                        String rawPassword = generateRandomPassword(8);
                        reader.setPassword(rawPassword);
                        reader.setStatus("APPROVED");
                        reader.setApprovedDate(LocalDateTime.now());
                        reader.setApprovedBy(currentLibrarian);

                        String subject = "Tài khoản thư viện của bạn đã được phê duyệt";
                        String body = "Xin chào " + reader.getFullName() + ",\n\n"
                                + "Tài khoản của bạn đã được phê duyệt thành công!\n"
                                + "Tên đăng nhập: " + reader.getUsername() + "\n"
                                + "Mật khẩu: " + rawPassword + "\n\n"
                                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n"
                                + "Thành thật xin cảm ơn,\nPhòng Thư viện";

                        // Gửi email
                        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);

                        // Lưu reader
                        save(reader);

                        successCount.incrementAndGet();

                    } catch (Exception e) {
                        e.printStackTrace();
                        failedCount.incrementAndGet();
                    }

                    // Cập nhật tiến độ
                    updateProgress(i + 1, total);
                }
                return null;
            }
        };

        // Khi hoàn thành
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            waitStage.close();
            String message = String.format(
                    "Hoàn tất phê duyệt!\nThành công: %d\nThất bại: %d",
                    successCount.get(), failedCount.get()
            );
            showAlert(Alert.AlertType.INFORMATION, "Phê duyệt hàng loạt", message);

            if (onSuccess != null) onSuccess.run();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            waitStage.close();
            Throwable ex = task.getException();
            showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Có lỗi xảy ra khi phê duyệt hàng loạt:\n" + (ex != null ? ex.getMessage() : "Unknown error"));

            if (onFailure != null) onFailure.run();
        }));

        task.setOnCancelled(e -> Platform.runLater(() -> {
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

    public void rejectMultipleByIds(Collection<Long> selectedIds, Runnable onSuccess, Runnable onFailure) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn reader", "Vui lòng chọn ít nhất một tài khoản để từ chối.");
            return;
        }

        // 1. Nhập lý do từ chối (chỉ nhập 1 lần cho toàn bộ)
        TextInputDialog reasonDialog = new TextInputDialog();
        reasonDialog.setTitle("Từ chối hàng loạt");
        reasonDialog.setHeaderText("Từ chối " + selectedIds.size() + " tài khoản");
        reasonDialog.setContentText("Nhập lý do từ chối (áp dụng cho tất cả):");
        Optional<String> reasonOpt = reasonDialog.showAndWait();
        if (reasonOpt.isEmpty()) return; // Người dùng bấm Cancel

        final String reason = reasonOpt.get().trim().isEmpty() ? "Không có lý do cụ thể" : reasonOpt.get().trim();

        // 2. Xác nhận hành động
        if (!confirmAction("Từ chối hàng loạt",
                "Bạn có chắc muốn TỪ CHỐI và XÓA " + selectedIds.size() + " tài khoản đã chọn?\n\nLý do: " + reason)) {
            return;
        }

        // 3. Tạo dialog chờ
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);

        Label statusLabel = new Label("Chuẩn bị từ chối...");
        Label detailLabel = new Label("0 / " + selectedIds.size());

        VBox box = new VBox(15,
                new Label("Đang từ chối và xóa tài khoản..."),
                statusLabel,
                detailLabel,
                progress);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 30; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");

        Stage waitStage = new Stage();
        waitStage.initModality(Modality.APPLICATION_MODAL);
        waitStage.initStyle(StageStyle.UNDECORATED);
        waitStage.setScene(new Scene(box));
        waitStage.sizeToScene();
        waitStage.show();

        // 4. Biến đếm
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);
        List<Long> idList = new ArrayList<>(selectedIds);
        int total = idList.size();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (Long readerId : idList) {
                    if (isCancelled()) break;

                    // Cập nhật UI an toàn (không lỗi final)
                    Platform.runLater(() -> {
                        statusLabel.setText("Đang xử lý reader ID: " + readerId);
                        detailLabel.setText(processed.incrementAndGet() + " / " + total);
                    });

                    try {
                        Reader reader = readerRepository.findById(readerId).orElse(null);
                        if (reader == null) {
                            failedCount.incrementAndGet();
                            continue;
                        }

                        // Gửi email từ chối
                        String subject = "Tài khoản thư viện của bạn đã bị từ chối";
                        String body = """
                            Xin chào %s,

                            Rất tiếc phải thông báo rằng tài khoản thư viện của bạn đã bị TỪ CHỐI.

                            Lý do từ chối: %s

                            Nếu bạn cần hỗ trợ hoặc muốn làm rõ thông tin, vui lòng liên hệ trực tiếp Trung tâm Thư viện trong giờ hành chính.

                            Trân trọng,
                            Phòng Thư viện
                            """.formatted(reader.getFullName(), reason);

                        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);

                        // Xóa tài khoản
                        delete(reader.getUserId());

                        successCount.incrementAndGet();

                    } catch (Exception e) {
                        e.printStackTrace();
                        failedCount.incrementAndGet();
                    }

                    updateProgress(processed.get(), total);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            waitStage.close();
            showAlert(Alert.AlertType.INFORMATION, "Hoàn tất từ chối",
                    "Đã xử lý xong!\n\nThành công: %d\nThất bại: %d".formatted(successCount.get(), failedCount.get()));
            if (onSuccess != null) onSuccess.run();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            waitStage.close();
            Throwable ex = task.getException();
            showAlert(Alert.AlertType.ERROR, "Lỗi khi từ chối hàng loạt",
                    ex != null ? ex.getMessage() : "Có lỗi không xác định xảy ra.");
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

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

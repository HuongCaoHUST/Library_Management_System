package com.example.project.controller;

import com.example.project.models.Doc;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.nio.file.Paths;

public class DocAddController {
    @FXML TextField txtTitle, txtAuthor, txtPublisher, txtPubYear, txtCategory, txtDocType, txtShelfLoc, txtAccessUrl;
    @FXML Button addBtn;
    Path DOCS_PATH = Paths.get("data/docs.txt");

    @FXML
    private void handleDocAdd(ActionEvent event) {
        try {
            String title = safe(txtTitle);
            String author = safe(txtAuthor);
            String publisher = safe(txtPublisher);
            String pubYear = safe(txtPubYear);
            String category = safe(txtCategory);
            String docType = safe(txtDocType);
            String shelfLoc = safe(txtShelfLoc);
            String accessUrl = safe(txtAccessUrl);

            if (!(docType.equals("in") || docType.equals("số"))) {
                showAlert("Loại tài liệu chỉ 'in' hoặc 'số'");
                return;
            }

            if (docType.equals("in")) {
                accessUrl = "";
                if (isBlank(shelfLoc)) {
                    showAlert("Tài liệu in cần nhập vị trí trên kệ");
                    return;
                }
            } else {
                shelfLoc = "";
                if (isBlank(accessUrl)) {
                    showAlert("Tài liệu số cần nhập đường dẫn truy cập");
                    return;
                }
            }

            String docId = nextDocId();
            String status = "còn";
            String[] parts = new String[] { docId, title, author, publisher, pubYear, category, docType, shelfLoc, accessUrl, status };
            Doc doc = new Doc(parts);
            writeDoc(doc);
            showAlert("Đã thêm tài liệu: " + docId);

            txtTitle.clear();
            txtAuthor.clear();
            txtPublisher.clear();
            txtPubYear.clear();
            txtCategory.clear();
            txtDocType.clear();
            txtShelfLoc.clear();
            txtAccessUrl.clear();

            Stage stage = (Stage) addBtn.getScene().getWindow();
            stage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi khi thêm tài liệu:\n" + ex.getMessage());
        }
    }

    private static String safe(TextField tf) {
        return tf == null ? "" : tf.getText().trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void writeDoc(Doc doc) throws IOException {
        Files.createDirectories(DOCS_PATH.getParent());
        String line = String.join(",",
                doc.getDocId(),
                doc.getTitle(),
                doc.getAuthor(),
                doc.getPublisher(),
                doc.getPubYear(),
                doc.getCategory(),
                doc.getDocType(),
                Optional.ofNullable(doc.getShelfLoc()).orElse(""),
                Optional.ofNullable(doc.getAccessUrl()).orElse(""),
                Optional.ofNullable(doc.getStatus()).orElse("")
        );
        try (BufferedWriter writer = Files.newBufferedWriter(DOCS_PATH, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }

    private String nextDocId() {
        String prefix = "DOC";
        int max = 0;
        if (Files.exists(DOCS_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(DOCS_PATH, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    String id = parts[0].trim();
                    if (id.startsWith(prefix)) {
                        String num = id.substring(prefix.length());
                        if (num.matches("\\d+")) {
                            int n = Integer.parseInt(num);
                            if (n > max) max = n;
                        }
                    }
                }
            } catch (IOException e) {
                // log hoặc báo lỗi tùy nhu cầu
            }
        }
        return prefix + String.format("%04d", max + 1);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
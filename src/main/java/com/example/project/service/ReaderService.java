package com.example.project.service;

import com.example.project.apiservice.ReaderApiService;
import com.example.project.model.Reader;
import javafx.scene.control.*;

import java.util.List;

public class ReaderService {
    private final ReaderApiService readerApiService = new ReaderApiService();

    public List<Reader> getApprovedReaders() {
        try {
            return readerApiService.filterReaders(null, null, "APPROVED", null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Reader> getPendingReaders() {
        try {
            return readerApiService.filterReaders(null, null, "PENDING", null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

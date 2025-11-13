package com.example.project.controller;

import com.example.project.model.Document;
import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import com.example.project.util.SendEmail;
import com.example.project.util.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
public class DocumentDetailController {
    @FXML private Label lblAuthor;
    @FXML private Label lblPublisher;
    @FXML private Label lblPublicationYear;
    @FXML private Label lblCategory;
    @FXML private Label lblDocumentType;
    @FXML private Label lblShelfLocation;
    @FXML private Label lblStatus;

    private Document currentDocument;
    @Autowired
    private ReaderService readerService;

    public void setDocument(Document document) {
        this.currentDocument = document;

        lblAuthor.setText(document.getAuthor());
        lblPublisher.setText(document.getPublisher());
        lblPublicationYear.setText(document.getPublicationYear().toString());
        lblCategory.setText(document.getCategory());
        lblDocumentType.setText(document.getDocumentType());
        lblShelfLocation.setText(document.getShelfLocation());
        lblStatus.setText(document.getStatus());

    }
}
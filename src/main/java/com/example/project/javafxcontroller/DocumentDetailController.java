package com.example.project.javafxcontroller;

import com.example.project.model.Document;
import com.example.project.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
package com.example.project.controller;

import com.example.project.model_controller.DocController;
import com.example.project.models.Doc;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DetailDocController {
    @FXML private ImageView imgCover;
    @FXML private Text lblDocId;
    @FXML private Text lblTitle;
    @FXML private Label lblAuthor;
    @FXML private Label lblPublisher;
    @FXML private Label lblPubYear;
    @FXML private Label lblCategory;
    @FXML private Label lblDocType;
    @FXML private Label lblShelfLoc;
    @FXML private Label lblAccessUrl;
    @FXML private Label lblStatus;
    private Doc currentDoc;
    private DocController docController;
    public void setDoc(Doc doc) {
        this.currentDoc = doc;

        lblDocId.setText(doc.getDocId());
        lblTitle.setText(doc.getTitle());
        lblAuthor.setText(doc.getAuthor());
        lblPublisher.setText(doc.getPublisher());
        lblPubYear.setText(doc.getPubYear());
        lblDocType.setText(doc.getDocType());
        lblCategory.setText(doc.getCategory());
        lblShelfLoc.setText(
                doc.getShelfLoc() == null || doc.getShelfLoc().isBlank() ? "" : doc.getShelfLoc()
        );
        lblAccessUrl.setText(
                doc.getAccessUrl() == null || doc.getAccessUrl().isBlank() ? "" : doc.getAccessUrl()
        );
        lblStatus.setText(
                doc.getStatus() == null || doc.getStatus().isBlank() ? "" : doc.getStatus()
        );

        // Ảnh minh họa mặc định cho tài liệu (tùy bạn đặt tên file)
        try {
            Image defaultImage = new Image(new FileInputStream("src/main/resources/com/example/project/logo/no_cover.png"));
            imgCover.setImage(defaultImage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDocController(DocController dc) {
        this.docController = dc;
    }

    private DocManagerController parentController;

    public void setParentController(DocManagerController parentController) {
        this.parentController = parentController;
    }
}

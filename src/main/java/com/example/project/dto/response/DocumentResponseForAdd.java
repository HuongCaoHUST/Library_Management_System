package com.example.project.dto.response;

import com.example.project.model.Document;
import lombok.Getter;

@Getter
public class DocumentResponseForAdd {

    private final Long documentId;
    private final String title;
    private final String author;
    private final String publisher;
    private final Integer publicationYear;
    private final String ddcNumber;
    private final String cutterCode;
    private final Integer availableCopies;
    private final Integer borrowedCopies;
    private final Double coverPrice;
    private final String classificationNumber;
    private final String category;
    private final String shelfLocation;
    private final String documentType;
    private final String accessLink;
    private final String status;

    public DocumentResponseForAdd(Document document) {
        this.documentId = document.getDocumentId();
        this.title = document.getTitle();
        this.author = document.getAuthor();
        this.publisher = document.getPublisher();
        this.publicationYear = document.getPublicationYear();
        this.ddcNumber = document.getDdcNumber();
        this.cutterCode = document.getCutterCode();
        this.availableCopies = document.getAvailableCopies();
        this.borrowedCopies = document.getBorrowedCopies();
        this.coverPrice = document.getCoverPrice();
        this.classificationNumber = document.getClassificationNumber();
        this.category = document.getCategory();
        this.shelfLocation = document.getShelfLocation();
        this.documentType = document.getDocumentType().getName();
        this.accessLink = document.getAccessLink();
        this.status = document.getStatus();
    }
}


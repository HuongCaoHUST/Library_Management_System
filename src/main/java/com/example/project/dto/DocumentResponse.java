package com.example.project.dto;

import lombok.Data;

@Data
public class DocumentResponse {
    private Long documentId;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String ddcNumber;
    private String cutterCode;
    private Integer availableCopies;
    private Integer borrowedCopies;
    private Integer totalCopies;
    private Double coverPrice;
    private String classificationNumber;
    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
}
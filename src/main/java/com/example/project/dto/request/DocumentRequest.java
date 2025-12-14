package com.example.project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentRequest {

    private String title;
    private String author;
    private String publisher;
    private String publicationYear;
    private String classificationNumber;
    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
    private Integer availableCopies;
}
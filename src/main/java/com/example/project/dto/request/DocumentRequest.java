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
    private String categoryName;
    private String shelfLocation;
    private String documentTypeName;
    private String accessLink;
    private String status;
    private Integer availableCopies;
}
package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}
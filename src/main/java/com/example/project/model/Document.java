package com.example.project.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "documentId")
public class Document {

    private Long documentId;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String ddcNumber;
    private String cutterCode;
    private Integer availableCopies;
    private Integer borrowedCopies;
    private Double coverPrice;
    private String classificationNumber;
    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
}

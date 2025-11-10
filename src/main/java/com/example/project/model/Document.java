package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String ddcNumber;
    private String cutterCode;
    @Column(nullable = false)
    private Integer availableCopies;
    @Column(nullable = false)
    private Integer borrowedCopies;
    private Double coverPrice;
    private String classificationNumber;
    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
}

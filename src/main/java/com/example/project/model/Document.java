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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String shelfLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    private String accessLink;
    private String status;
}

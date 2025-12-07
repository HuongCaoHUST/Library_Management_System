package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // ← Thêm import
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grn_details")
public class GrnDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_receipt_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore  // ← Thêm annotation này để ngăn serialize trường này
    private Grn grn;

    private String category;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String shelfLocation;
    private Integer availableCopies;
    private Double coverPrice;
    private String dkcbCode;
}
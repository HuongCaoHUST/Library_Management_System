package com.example.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class DocumentPatchRequest {
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;

    // XÓA 3 TRƯỜNG:
    // private String ddcNumber;
    // private String cutterCode;
    // private String classificationNumber;

    @Min(value = 0, message = "Số bản có sẵn phải >= 0")
    private Integer availableCopies;

    @Min(value = 0, message = "Số bản đang mượn phải >= 0")
    private Integer borrowedCopies;

    @PositiveOrZero
    private Double coverPrice;

    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
}
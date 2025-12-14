// src/main/java/com/example/project/dto/DocumentPatchRequest.java
package com.example.project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class DocumentPatchRequest {

    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String ddcNumber;
    private String cutterCode;

    @Min(value = 0, message = "Số bản có sẵn phải >= 0")
    private Integer availableCopies;

    @Min(value = 0, message = "Số bản đang mượn phải >= 0")
    private Integer borrowedCopies;

    @PositiveOrZero
    private Double coverPrice;

    private String classificationNumber;
    private String category;
    private String shelfLocation;
    private String documentType;
    private String accessLink;
    private String status;
}
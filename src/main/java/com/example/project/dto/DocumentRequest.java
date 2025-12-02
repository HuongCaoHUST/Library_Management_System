package com.example.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotBlank(message = "Mã DKCB không được để trống")
    private String documentId;  // THÊM FIELD MỚI - Librarian sẽ nhập mã này

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    private String publisher;

    @Min(value = 0) @Max(value = 9999)
    private Integer publicationYear;

    // XÓA 3 TRƯỜNG:
    // private String ddcNumber;
    // private String cutterCode;
    // private String classificationNumber;

    @NotNull @Min(0)
    private Integer availableCopies;

    @Min(0)
    private Integer borrowedCopies = 0;

    @PositiveOrZero
    private Double coverPrice;

    private String category;

    private String shelfLocation;

    private String documentType;

    private String accessLink;

    private String status;
}
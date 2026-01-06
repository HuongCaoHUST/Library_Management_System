package com.example.project.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GRNDetailRequest {

    private Long documentId;
    private Integer quantity;
    private Double unitPrice;
    private String note;
}
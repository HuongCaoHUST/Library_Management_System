package com.example.project.dto.response;

import lombok.Data;

@Data
public class GRNDetailResponse {

    private Long documentId;
    private String documentTitle;
    private Integer quantity;
    private Double unitPrice;
}

package com.example.project.dto.response;

import lombok.Data;

@Data
public class BorrowSlipDetailResponse {

    private Long documentId;
    private String documentTitle;
    private Integer quantity;
}

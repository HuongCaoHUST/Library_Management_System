package com.example.project.dto.request;

import lombok.Data;

@Data
public class BorrowSlipDetailRequest {

    private Long documentId;
    private Integer quantity;
}

package com.example.project.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GRNResponse {

    private Long grnId;
    private Long invoiceId;
    private String librarianName;
    private String supplierName;
    private String deliverer;
    private LocalDate receiptDate;

    private List<GRNDetailResponse> details;
}

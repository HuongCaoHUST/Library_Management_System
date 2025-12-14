package com.example.project.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GRNRequest {

    private Long invoiceId;
    private Long librarianId;
    private Long supplierId;
    private String deliverer;
    private LocalDate receiptDate;

    private List<GRNDetailRequest> details;
}

package com.example.project.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BorrowSlipRequest {

    private Long readerId;
    private LocalDate dueDate;
    private List<BorrowSlipDetailRequest> details;
}

package com.example.project.dto.response;

import com.example.project.model.BorrowSlip;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BorrowSlipResponse {

    private Long borrowSlipId;
    private Long readerId;
    private String readerName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private BorrowSlip.BorrowStatus status;

    private List<BorrowSlipDetailResponse> details;
}

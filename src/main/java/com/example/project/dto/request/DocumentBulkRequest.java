package com.example.project.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentBulkRequest {

    @NotEmpty(message = "Danh sách tài liệu không được rỗng")
    @Valid
    private List<DocumentRequest> documents;
}
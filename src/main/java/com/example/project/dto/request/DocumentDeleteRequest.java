package com.example.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentDeleteRequest {

    @NotEmpty(message = "Danh sách ID không được rỗng")
    private List<Long> documentIds;
}
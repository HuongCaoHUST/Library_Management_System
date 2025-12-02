package com.example.project.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentDeleteRequest {

    @NotEmpty(message = "Danh sách ID không được rỗng")
    private List<String> documentIds;
}
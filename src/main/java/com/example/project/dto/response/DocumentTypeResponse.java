package com.example.project.dto.response;

import com.example.project.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeResponse {
    private String documentTypeName;
    private String description;
    public DocumentTypeResponse(Category category) {
        this.documentTypeName = category.getName();
        this.description = category.getDescription();
    }
}
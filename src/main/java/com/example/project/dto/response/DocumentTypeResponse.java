package com.example.project.dto.response;

import com.example.project.model.Category;
import com.example.project.model.DocumentType;
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

    public DocumentTypeResponse(DocumentType documentType) {
        this.documentTypeName = documentType.getName();
        this.description = documentType.getDescription();
    }
}
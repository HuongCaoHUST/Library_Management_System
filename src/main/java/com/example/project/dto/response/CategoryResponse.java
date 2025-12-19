package com.example.project.dto.response;

import com.example.project.model.Category;
import com.example.project.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String categoryName;
    private String description;
    public CategoryResponse(Category category) {
        this.categoryName = category.getName();
        this.description = category.getDescription();
    }
}
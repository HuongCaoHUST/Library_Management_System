package com.example.project.dto.response;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaderResponse {
    private Long readerId;
    private String fullName;
    public ReaderResponse(Reader reader) {
        this.readerId = reader.getUserId();
        this.fullName = reader.getFullName();
    }
}

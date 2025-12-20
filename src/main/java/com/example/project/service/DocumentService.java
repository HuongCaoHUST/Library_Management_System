package com.example.project.service;

import com.example.project.dto.request.DocumentRequest;
import com.example.project.dto.response.DocumentResponse;
import com.example.project.dto.response.DocumentResponseForAdd;
import com.example.project.mapper.DocumentMapper;
import com.example.project.model.BorrowSlip;
import com.example.project.model.Category;
import com.example.project.model.Document;
import com.example.project.model.DocumentType;
import com.example.project.repository.CategoryRepository;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.DocumentTypeRepository;
import com.example.project.specification.DocumentSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentMapper documentMapper;

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public Document save(Document document) {
        return documentRepository.save(document);
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    public List<Document> findAllById(List<Long> ids) {
        return documentRepository.findAllById(ids);
    }

    public List<Document> saveAll(List<Document> documents) {
        return documentRepository.saveAll(documents);
    }

    public void deleteAllById(List<Long> ids) {
        documentRepository.deleteAllById(ids);
    }

    public List<Document> filterDocuments(String title, String author, String publisher, Long documentTypeId, Integer publicationYear) {
        Specification<Document> spec = Specification
                .where(DocumentSpecification.hasTitle(title))
                .and(DocumentSpecification.hasAuthor(author))
                .and(DocumentSpecification.hasPublisher(publisher))
                .and(DocumentSpecification.hasDocumentTypeId(documentTypeId))
                .and(DocumentSpecification.hasPublicationYear(publicationYear));

        return documentRepository.findAll(spec);
    }

    @Transactional
    public DocumentResponse create(DocumentRequest request) {

        Document document = documentMapper.toEntity(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category không tồn tại"));

        DocumentType documentType = documentTypeRepository.findById(
                        request.getDocumentTypeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("DocumentType không tồn tại"));

        document.setCategory(category);
        document.setDocumentType(documentType);
        Document saved = documentRepository.save(document);

        return documentMapper.toResponse(saved);
    }

    public ByteArrayInputStream exportDocumentsToExcel() {
        try (
                InputStream templateStream =
                        getClass().getResourceAsStream("/templates/document_export_template.xlsx");
                Workbook workbook = new XSSFWorkbook(templateStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Document> documents = documentRepository.findAll();

            int rowIndex = 1;

            for (Document doc : documents) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(doc.getTitle());
                row.createCell(1).setCellValue(doc.getAuthor());
                row.createCell(2).setCellValue(
                        doc.getPublisher() != null ? doc.getPublisher() : ""
                );

                if (doc.getPublicationYear() != null) {
                    row.createCell(3).setCellValue(doc.getPublicationYear());
                } else {
                    row.createCell(3).setCellValue("");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Xuất Excel thất bại", e);
        }
    }
}

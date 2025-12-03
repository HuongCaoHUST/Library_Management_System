package com.example.project.service;

import com.example.project.model.Document;
import com.example.project.model.Grn;
import com.example.project.model.GrnDetail;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.GrnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GrnService {

    private final GrnRepository grnRepository;
    private final DocumentRepository documentRepository;

    public GrnService(GrnRepository grnRepository, DocumentRepository documentRepository) {
        this.grnRepository = grnRepository;
        this.documentRepository = documentRepository;
    }

    /**
     * Lưu phiếu nhập kho và tự động tạo/cập nhật tài liệu
     */
    @Transactional
    public Grn saveGrn(Grn grn) {
        // Kiểm tra receiptId đã tồn tại chưa
        if (grnRepository.existsById(grn.getReceiptId())) {
            throw new IllegalArgumentException("Mã hoá đơn đã tồn tại: " + grn.getReceiptId());
        }

        // Lưu phiếu nhập
        Grn savedGrn = grnRepository.save(grn);

        // Tự động tạo hoặc cập nhật tài liệu từ GRN
        for (GrnDetail detail : savedGrn.getItems()) {
            createOrUpdateDocumentFromGrnDetail(detail);
        }

        return savedGrn;
    }

    /**
     * Tạo hoặc cập nhật tài liệu từ chi tiết phiếu nhập
     */
    private void createOrUpdateDocumentFromGrnDetail(GrnDetail detail) {
        // Tìm tài liệu theo mã ĐKCB (nếu có)
        Optional<Document> existingDoc = Optional.empty();
        if (detail.getDkcbCode() != null && !detail.getDkcbCode().trim().isEmpty()) {
            existingDoc = documentRepository.findByDkcbCode(detail.getDkcbCode());
        }

        if (existingDoc.isPresent()) {
            // Cập nhật số lượng nếu tài liệu đã tồn tại
            Document doc = existingDoc.get();
            doc.setAvailableCopies(doc.getAvailableCopies() + detail.getAvailableCopies());
            documentRepository.save(doc);
        } else {
            // ✅ Tạo mới tài liệu (THÊM PUBLISHER)
            Document newDoc = Document.builder()
                    .title(detail.getTitle())
                    .author(detail.getAuthor())
                    .publisher(detail.getPublisher())  // ✅ THÊM DÒNG NÀY
                    .publicationYear(detail.getPublicationYear())
                    .category(detail.getCategory())
                    .shelfLocation(detail.getShelfLocation())
                    .availableCopies(detail.getAvailableCopies())
                    .borrowedCopies(0)
                    .coverPrice(detail.getCoverPrice() != null ? detail.getCoverPrice().doubleValue() : null)
                    .dkcbCode(detail.getDkcbCode())
                    .status("Được mượn")
                    .documentType("Tài liệu In")
                    .build();

            documentRepository.save(newDoc);
        }
    }

    public List<Grn> findAll() {
        return grnRepository.findAll();
    }

    public Optional<Grn> findByReceiptId(String receiptId) {
        return grnRepository.findById(receiptId);
    }

    public List<Grn> findBySupplier(String supplier) {
        return grnRepository.findBySupplier(supplier);
    }

    public List<Grn> findByReceiveDate(LocalDate receiveDate) {
        return grnRepository.findByReceiveDate(receiveDate);
    }

    @Transactional
    public void deleteByReceiptId(String receiptId) {
        grnRepository.deleteById(receiptId);
    }
}
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
import java.util.Objects;
import java.util.Optional;

@Service
public class GrnService {

    private final GrnRepository grnRepository;
    private final DocumentRepository documentRepository;

    public GrnService(GrnRepository grnRepository, DocumentRepository documentRepository) {
        this.grnRepository = grnRepository;
        this.documentRepository = documentRepository;
    }

    // Phương thức để kiểm tra thông tin tài liệu
    public void validateDocumentInput(List<GrnDetail> details) {
        StringBuilder errorMessages = new StringBuilder();

        for (int i = 0; i < details.size(); i++) {
            GrnDetail detail = details.get(i);
            StringBuilder tempMessage = new StringBuilder();

            // Kiểm tra tài liệu trong cơ sở dữ liệu bằng title, author, publisher, publicationYear
            Optional<Document> existingDoc = documentRepository.findByTitleAndAuthorAndPublisherAndPublicationYear(
                    detail.getTitle(), detail.getAuthor(), detail.getPublisher(), detail.getPublicationYear()
            );

            // Trường hợp trùng lặp thông tin nhưng khác mã DKCB
            if (existingDoc.isPresent() && !existingDoc.get().getDkcbCode().equals(detail.getDkcbCode())) {
                tempMessage.append(String.format("Tài liệu %d: Thông tin tài liệu đã tồn tại nhưng khác mã DKCB.",
                        i + 1));
                errorMessages.append(tempMessage).append("\n\r");
            }

            // Kiểm tra trường hợp mã DKCB đã tồn tại
            Optional<Document> docByDkcb = documentRepository.findByDkcbCode(detail.getDkcbCode());
            if (docByDkcb.isPresent()) {
                Document existingDocument = docByDkcb.get();
                StringBuilder mismatchInfo = new StringBuilder();
                boolean hasMismatch = false;

                // So sánh thông tin từng thuộc tính và xây dựng thông báo cụ thể
                if (!existingDocument.getTitle().equals(detail.getTitle())) {
                    mismatchInfo.append("Tiêu đề");
                    hasMismatch = true;
                }
                if (!existingDocument.getAuthor().equals(detail.getAuthor())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Tác giả");
                    hasMismatch = true;
                }
                if (!Objects.equals(existingDocument.getPublicationYear(), detail.getPublicationYear())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Năm xuất bản");
                    hasMismatch = true;
                }
                if (!existingDocument.getCategory().equals(detail.getCategory())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Loại tài liệu");
                    hasMismatch = true;
                }
                if (!Objects.equals(existingDocument.getCoverPrice(), detail.getCoverPrice())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Giá bìa");
                    hasMismatch = true;
                }
                if (!existingDocument.getShelfLocation().equals(detail.getShelfLocation())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Vị trí kệ");
                    hasMismatch = true;
                }
                if (!existingDocument.getPublisher().equals(detail.getPublisher())) {
                    if (hasMismatch) {
                        mismatchInfo.append("; ");
                    }
                    mismatchInfo.append("Nhà xuất bản");
                    hasMismatch = true;
                }

                // Ném ra lỗi nếu có thông tin không khớp
                if (hasMismatch) {
                    String message = String.format("Tài liệu %d: Mã DKCB đã tồn tại nhưng khác thông tin: %s.",
                            i + 1, mismatchInfo);
                    errorMessages.append(message).append("\n\r");
                }
            }
        }

        // Nếu có thông báo lỗi, ném ra với các lý do cụ thể
        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(errorMessages.toString());
        }
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

        // Gọi hàm kiểm tra thông tin tài liệu
        validateDocumentInput(grn.getItems());

        // Lưu phiếu nhập và thực hiện các logic khác
        Grn savedGrn = grnRepository.save(grn);
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
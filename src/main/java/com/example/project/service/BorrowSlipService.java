package com.example.project.service;

import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.mapper.BorrowSlipMapper;
import com.example.project.model.*;
import com.example.project.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowSlipService {

    private final BorrowSlipRepository borrowSlipRepository;
    private final ReaderRepository readerRepository;
    private final DocumentRepository documentRepository;
    private final BorrowSlipMapper mapper;

    @Transactional
    public BorrowSlipResponse create(BorrowSlipRequest request) {

        BorrowSlip borrowSlip = mapper.toEntity(request);

        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bạn đọc"));

        borrowSlip.setReader(reader);

        List<BorrowSlipDetail> details = request.getDetails().stream()
                .map(d -> {
                    Document document = documentRepository.findById(d.getDocumentId())
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu với id: " + d.getDocumentId()));

                    if (document.getAvailableCopies() < d.getQuantity()) {
                        throw new IllegalArgumentException("Không đủ số lượng tài liệu '" + document.getTitle() + "' cho mượn.");
                    }

                    document.setAvailableCopies(document.getAvailableCopies() - d.getQuantity());
                    document.setBorrowedCopies(document.getBorrowedCopies() + d.getQuantity());
                    documentRepository.save(document);

                    BorrowSlipDetail detail = new BorrowSlipDetail();
                    detail.setBorrowSlip(borrowSlip);
                    detail.setDocument(document);
                    detail.setQuantity(d.getQuantity());
                    return detail;
                }).toList();

        borrowSlip.setDetails(details);
        BorrowSlip saved = borrowSlipRepository.save(borrowSlip);

        return mapper.toResponse(saved);
    }

}

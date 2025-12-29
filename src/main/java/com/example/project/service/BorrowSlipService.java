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
                    BorrowSlipDetail detail = new BorrowSlipDetail();
                    detail.setBorrowSlip(borrowSlip);
                    detail.setDocument(documentRepository.findById(d.getDocumentId()).orElseThrow());
                    detail.setQuantity(d.getQuantity());
                    return detail;
                }).toList();

        borrowSlip.setDetails(details);
        BorrowSlip saved = borrowSlipRepository.save(borrowSlip);

        return mapper.toResponse(saved);
    }

}


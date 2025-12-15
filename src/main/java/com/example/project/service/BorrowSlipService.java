package com.example.project.service;

import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.mapper.BorrowSlipMapper;
import com.example.project.model.*;
import com.example.project.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowSlipService {

    private final BorrowSlipRepository borrowSlipRepository;
    private final ReaderRepository readerRepository;
    private final DocumentRepository documentRepository;
    private final BorrowSlipMapper mapper;

    public BorrowSlipService(BorrowSlipRepository borrowSlipRepository,
                             ReaderRepository readerRepository,
                             DocumentRepository documentRepository,
                             BorrowSlipMapper mapper) {
        this.borrowSlipRepository = borrowSlipRepository;
        this.readerRepository = readerRepository;
        this.documentRepository = documentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public BorrowSlip create(BorrowSlipRequest request) {

        BorrowSlip borrowSlip = mapper.toEntity(request);

        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow();

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
        return borrowSlipRepository.save(borrowSlip);
    }

}


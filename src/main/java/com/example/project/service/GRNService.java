package com.example.project.service;

import com.example.project.dto.request.GRNRequest;
import com.example.project.mapper.GRNMapper;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.mapper.SupplierMapper;
import com.example.project.model.GRN;
import com.example.project.model.GRNDetail;
import com.example.project.model.Librarian;
import com.example.project.model.Supplier;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.GRNRepository;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GRNService {

    private final GRNRepository grnRepository;
    private final SupplierRepository supplierRepository;
    private final LibrarianRepository librarianRepository;
    private final DocumentRepository documentRepository;
    private final GRNMapper mapper;

    public GRNService(GRNRepository grnRepository,
                      SupplierRepository supplierRepository,
                      LibrarianRepository librarianRepository,
                      DocumentRepository documentRepository,
                      GRNMapper mapper) {
        this.grnRepository = grnRepository;
        this.supplierRepository = supplierRepository;
        this.librarianRepository = librarianRepository;
        this.documentRepository = documentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public GRN create(GRNRequest request) {

        GRN grn = mapper.toEntity(request);

        Librarian librarian = librarianRepository.findById(request.getLibrarianId())
                .orElseThrow();
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow();

        grn.setReceiver(librarian);
        grn.setSupplier(supplier);

        List<GRNDetail> details = request.getDetails().stream()
                .map(d -> {
                    GRNDetail detail = new GRNDetail();
                    detail.setGrn(grn);
                    detail.setDocument(documentRepository.findById(d.getDocumentId()).orElseThrow());
                    detail.setQuantity(d.getQuantity());
                    detail.setUnitPrice(d.getUnitPrice());
                    detail.setNote(d.getNote());
                    return detail;
                }).toList();

        grn.setDetails(details);
        return grnRepository.save(grn);
    }

}


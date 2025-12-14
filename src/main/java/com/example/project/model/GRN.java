package com.example.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grn")
public class GRN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnId;

    private Long invoiceId;

    @ManyToOne
    @JoinColumn(name = "librarian_id")
    private Librarian receiver;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String deliverer;

    private LocalDate receiptDate;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GRNDetail> details;
}

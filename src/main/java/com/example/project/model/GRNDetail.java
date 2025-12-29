package com.example.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grn_detail")
public class GRNDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnDetailId;

    @ManyToOne
    @JoinColumn(name = "grn_id")
    private GRN grn;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private Integer quantity;

    private Double unitPrice;

    private String note;
}

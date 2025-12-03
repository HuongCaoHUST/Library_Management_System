package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grn_details")
public class GrnDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_receipt_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Grn grn;

    private String category;           // Thể loại

    @Column(nullable = false)
    private String title;              // Nhan đề

    private String author;             // Tác giả

    // ✅ THÊM TRƯỜNG MỚI: Nhà xuất bản
    private String publisher;          // Nhà xuất bản

    private Integer publicationYear;   // Năm xuất bản

    @Column(nullable = false)
    private String shelfLocation;      // Vị trí kệ

    @Column(nullable = false)
    private Integer availableCopies;   // Số lượng

    private Long coverPrice;           // Đơn giá

    private String dkcbCode;           // Mã ĐKCB
}
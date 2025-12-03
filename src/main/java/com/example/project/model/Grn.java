package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grns")
public class Grn {

    // ✅ SỬ DỤNG receipt_id LÀM KHOÁ CHÍNH (String)
    @Id
    @Column(name = "receipt_id", nullable = false, unique = true, length = 50)
    private String receiptId;          // Mã hoá đơn, đồng thời là khóa chính

    @Column(nullable = false)
    private String supplier;           // Đơn vị cung cấp

    @Column(nullable = false)
    private String receiver;           // Bên nhận

    @Column(nullable = false)
    private String deliverer;          // Bên giao

    @Column(nullable = false)
    private LocalDate receiveDate;     // Ngày nhận

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GrnDetail> items = new ArrayList<>();

    // Helper method để thêm item và duy trì quan hệ 2 chiều
    public void addItem(GrnDetail item) {
        items.add(item);
        item.setGrn(this);
    }

    public void removeItem(GrnDetail item) {
        items.remove(item);
        item.setGrn(null);
    }
}
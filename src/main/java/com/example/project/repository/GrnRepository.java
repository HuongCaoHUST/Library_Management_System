package com.example.project.repository;

import com.example.project.model.Grn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrnRepository extends JpaRepository<Grn, String> {  // ✅ Kiểu khoá chính là String

    // Tìm theo đơn vị cung cấp
    List<Grn> findBySupplier(String supplier);

    // Tìm theo ngày nhận
    List<Grn> findByReceiveDate(LocalDate receiveDate);

    // Tìm theo khoảng thời gian
    List<Grn> findByReceiveDateBetween(LocalDate startDate, LocalDate endDate);

    // Check tồn tại theo receiptId (đã có sẵn từ JpaRepository: existsById)
}
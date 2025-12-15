package com.example.project.repository;

import com.example.project.model.BorrowSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowSlipRepository extends JpaRepository<BorrowSlip, Long> {

    @Query("SELECT b FROM BorrowSlip b WHERE b.status = 'BORROWING' AND b.dueDate < :today")
    List<BorrowSlip> findOverdueSlips(@Param("today") LocalDate today);
}

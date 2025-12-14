package com.example.project.repository;

import com.example.project.model.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GRNRepository extends JpaRepository<GRN, Long>, JpaSpecificationExecutor<GRN> {
}

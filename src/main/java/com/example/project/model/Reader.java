package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "readers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Reader extends User {

    private LocalDateTime approvedDate;
    private LocalDateTime expirationDate;
    private BigDecimal depositAmount;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Librarian approvedBy;
}

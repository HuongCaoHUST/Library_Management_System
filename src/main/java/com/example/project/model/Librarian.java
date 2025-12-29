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
@Table(name = "librarians")
@PrimaryKeyJoinColumn(name = "user_id")
public class Librarian extends User {

    private LocalDateTime approvedDate;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private BigDecimal depositAmount;
}

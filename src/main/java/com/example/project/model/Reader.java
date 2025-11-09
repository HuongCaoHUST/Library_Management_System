package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "readers")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private String idCardNumber;
    private String placeOfBirth;
    private String issuedPlace;
    private String major;
    private String workPlace;
    private String address;

    private String username;
    private String password;
    private LocalDateTime registrationDate;
    private LocalDateTime approvedDate;
    private String status;
    private BigDecimal depositAmount;
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Librarian approvedBy;
}
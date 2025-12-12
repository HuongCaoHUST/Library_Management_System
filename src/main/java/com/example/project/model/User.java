package com.example.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

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
    @JsonIgnore
    private String password;
    private LocalDateTime registrationDate;
    private String status;

    @Enumerated(EnumType.STRING)
    private Role role;
}

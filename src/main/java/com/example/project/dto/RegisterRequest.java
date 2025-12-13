package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class RegisterRequest {

    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private String idCardNumber;
    private String placeOfBirth;
    private String issuedPlace;
    private String role;
    private String major;
    private String workPlace;
    private String address;
}
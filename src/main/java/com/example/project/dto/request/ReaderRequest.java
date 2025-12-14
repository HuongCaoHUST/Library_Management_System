package com.example.project.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReaderRequest {
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
}

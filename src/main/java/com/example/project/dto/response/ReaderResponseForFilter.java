package com.example.project.dto.response;

import com.example.project.model.Reader;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class ReaderResponseForFilter {
    Long userId;
    String fullName;
    String gender;
    LocalDate birthDate;
    String phoneNumber;
    String email;
    String idCardNumber;
    String placeOfBirth;
    String issuedPlace;
    String major;
    String workPlace;
    String address;
    String status;
    String role;
    LocalDateTime approvedDate;
    LocalDateTime expirationDate;
    BigDecimal depositAmount;
    String approvedBy;

    public ReaderResponseForFilter(Reader reader) {
        this.userId = reader.getUserId();
        this.fullName = reader.getFullName();
        this.gender = reader.getGender();
        this.birthDate = reader.getBirthDate();
        this.phoneNumber = reader.getPhoneNumber();
        this.email = reader.getEmail();
        this.idCardNumber = reader.getIdCardNumber();
        this.placeOfBirth = reader.getPlaceOfBirth();
        this.issuedPlace = reader.getIssuedPlace();
        this.major = reader.getMajor();
        this.workPlace = reader.getWorkPlace();
        this.address = reader.getAddress();
        this.status = reader.getStatus();
        this.role = reader.getRole().name();
        this.approvedDate = reader.getApprovedDate();
        this.expirationDate = reader.getExpirationDate();
        this.depositAmount = reader.getDepositAmount();
        this.approvedBy = reader.getApprovedBy().getFullName();
    }
}

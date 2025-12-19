package com.example.project.dto.response;

import com.example.project.model.Librarian;
import lombok.Value;
import java.time.LocalDate;

@Value
public class LibrarianResponseForFilter {
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
    String approvedBy;

    public LibrarianResponseForFilter(Librarian librarian) {
        this.userId = librarian.getUserId();
        this.fullName = librarian.getFullName();
        this.gender = librarian.getGender();
        this.birthDate = librarian.getBirthDate();
        this.phoneNumber = librarian.getPhoneNumber();
        this.email = librarian.getEmail();
        this.idCardNumber = librarian.getIdCardNumber();
        this.placeOfBirth = librarian.getPlaceOfBirth();
        this.issuedPlace = librarian.getIssuedPlace();
        this.major = librarian.getMajor();
        this.workPlace = librarian.getWorkPlace();
        this.address = librarian.getAddress();
        this.status = librarian.getStatus();
        this.role = librarian.getRole().getName();
        this.approvedBy = librarian.getApprovedBy().getFullName();
    }
}

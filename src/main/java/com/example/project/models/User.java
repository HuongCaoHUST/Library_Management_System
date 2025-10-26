package com.example.project.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class User {
    private final String userId;
    private final String fullName;
    private final Gender gender;
    private final LocalDate birthDate;
    private final String phoneNumber;
    private final String email;
    private final String idCardNumber;
    private final String placeOfBirth;
    private final String issuedPlace;
    private final String major;
    private final String workPlace;
    private final String address;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public User(String[] parts) {
        this.userId = parts[0].trim();
        this.fullName = parts[1].trim();
        this.gender = parseGender(parts[2].trim());
        this.birthDate = parseDate(parts[3]);
        this.phoneNumber = parts[4].trim();
        this.email = parts[5].trim();
        this.idCardNumber = parts[6].trim();
        this.placeOfBirth = parts[7].trim();
        this.issuedPlace = parts[8].trim();
        this.major = parts[9].trim();
        this.workPlace = parts[10].trim();
        this.address = parts[11].trim();
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e1) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }

    private Gender parseGender(String gender) {
        switch (gender.toLowerCase()) {
            case "nam": return Gender.MALE;
            case "nữ": return Gender.FEMALE;
            default: return Gender.OTHER;
        }
    }

    // getters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public Gender getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getIdCardNumber() { return idCardNumber; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public String getIssuedPlace() { return issuedPlace; }
    public String getMajor() { return major; }
    public String getWorkPlace() { return workPlace; }
    public String getAddress() { return address; }
}
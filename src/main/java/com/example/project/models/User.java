package com.example.project.models;

public class User {
    private final String studentId;
    private final String fullName;
    private final String gender;
    private final String birthDate;
    private final String phoneNumber;
    private final String email;
    private final String idCardNumber;
    private final String placeOfBirth;
    private final String issuedPlace;
    private final String major;
    private final String workPlace;
    private final String address;

    public User(String[] parts) {
        this.studentId = parts[0].trim();
        this.fullName = parts[1].trim();
        this.gender = parts[2].trim();
        this.birthDate = parts[3].trim();
        this.phoneNumber = parts[4].trim();
        this.email = parts[5].trim();
        this.idCardNumber = parts[6].trim();
        this.placeOfBirth = parts[7].trim();
        this.issuedPlace = parts[8].trim();
        this.major = parts[9].trim();
        this.workPlace = parts[10].trim();
        this.address = parts[11].trim();
    }

    // getters
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getIdCardNumber() { return idCardNumber; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public String getIssuedPlace() { return issuedPlace; }
    public String getMajor() { return major; }
    public String getWorkPlace() { return workPlace; }
    public String getAddress() { return address; }
}

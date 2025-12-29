package com.example.project.dto.request;

import lombok.Data;

@Data
public class SupplierRequest {
    private String supplierName;
    private String phoneNumber;
    private String email;
    private String contactPerson;
    private String address;
}

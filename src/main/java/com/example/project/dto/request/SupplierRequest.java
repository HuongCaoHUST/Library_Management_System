package com.example.project.dto.request;

import lombok.Data;

@Data
public class SupplierRequest {
    private String supplierName;
    private String phone;
    private String email;
    private String contactPerson;
    private String address;
}

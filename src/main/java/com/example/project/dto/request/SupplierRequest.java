package com.example.project.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest {
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
}
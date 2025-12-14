package com.example.project.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "supplierId")
public class Supplier {
    private Long supplierId;
    private String supplierName;
    private String contactName;
    private String phoneNumber;
    private String email;
    private String address;
}

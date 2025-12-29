package com.example.project.specification;

import com.example.project.model.Supplier;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification {

    public static Specification<Supplier> hasSupplierName(String supplyername) {
        return (root, query, cb) ->
                supplyername == null || supplyername.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("supplyername")), "%" + supplyername.toLowerCase() + "%");
    }

    public static Specification<Supplier> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) ->
                phoneNumber == null || phoneNumber.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("phoneNumber")), "%" + phoneNumber.toLowerCase() + "%");
    }
}

package com.example.project.specification;

import com.example.project.model.Reader;
import org.springframework.data.jpa.domain.Specification;

public class ReaderSpecification {

    public static Specification<Reader> hasFullName(String fullName) {
        return (root, query, cb) ->
                fullName == null || fullName.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<Reader> hasEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Reader> hasStatus(String status) {
        return (root, query, cb) ->
                status == null || status.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Reader> hasGender(String gender) {
        return (root, query, cb) ->
                gender == null || gender.isEmpty()
                        ? cb.conjunction()
                        : cb.equal(root.get("gender"), gender);
    }
}

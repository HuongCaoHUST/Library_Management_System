package com.example.project.security;

import java.util.EnumSet;
import java.util.Set;

public class RolePermissionMapper {

    public static Set<Permission> getPermissions(String role) {

        return switch (role) {
            case "ADMIN" -> EnumSet.allOf(Permission.class);

            case "LIBRARIAN" -> EnumSet.of(
                    Permission.READER_VIEW,
                    Permission.READER_CREATE,
                    Permission.READER_UPDATE,
                    Permission.READER_DELETE,

                    Permission.DOCUMENT_VIEW,
                    Permission.DOCUMENT_CREATE,
                    Permission.DOCUMENT_UPDATE,
                    Permission.DOCUMENT_DELETE
            );

            case "READER" -> EnumSet.of(
                    Permission.DOCUMENT_VIEW
            );

            default -> Set.of();
        };
    }
}

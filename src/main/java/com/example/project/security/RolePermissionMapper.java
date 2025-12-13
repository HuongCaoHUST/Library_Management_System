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

                    Permission.BOOK_VIEW,
                    Permission.BOOK_CREATE,
                    Permission.BOOK_UPDATE,
                    Permission.BOOK_DELETE
            );

            case "READER" -> EnumSet.of(
                    Permission.BOOK_VIEW
            );

            default -> Set.of();
        };
    }
}

package com.example.project.security;

public class PermissionService {
    public boolean canDeleteLibrarian() {
        return UserSession.getInstance().hasPermission("Xóa thủ thư");
    }

    public boolean canAddLibrarian() {
        return UserSession.getInstance().hasPermission("Thêm thủ thư");
    }

    public boolean canViewLibrarianList() {
        return UserSession.getInstance().hasPermission("Xem thủ thư");
    }

    public boolean canViewReaderList() {
        return UserSession.getInstance().hasPermission("Xem bạn đọc");
    }
}

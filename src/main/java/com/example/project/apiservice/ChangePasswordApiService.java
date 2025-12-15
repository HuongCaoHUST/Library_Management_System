package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.security.UserSession;

public class ChangePasswordApiService {

    private final ReaderApiService readerApi = new ReaderApiService();
    private final LibrarianApiService librarianApi = new LibrarianApiService();

    public ApiResponse<Void> changeMyPassword(
            String oldPassword,
            String newPassword) throws Exception {

        String role = UserSession.getInstance().getRole();

        return switch (role) {
            case "ADMIN" ->
                    librarianApi.changeMyPassword(oldPassword, newPassword);
            case "LIBRARIAN" ->
                    librarianApi.changeMyPassword(oldPassword, newPassword);
            case "READER" ->
                    readerApi.changeMyPassword(oldPassword, newPassword);
            default ->
                    throw new IllegalStateException("Bạn không thể đổi mật khẩu: " + role);
        };
    }
}

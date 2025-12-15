package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.model.Reader;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URLEncoder;
import java.util.List;

public class ReaderApiService extends BaseApiService {
    private static final String BASE_FILTER_URL =
            "http://localhost:8081/api/readers/filter";

    private static final String REGISTER_URL =
            "http://localhost:8081/api/readers/register";

    private static final String ME_URL =
            "http://localhost:8081/api/readers/me";

    private static final String CHANGE_PASSWORD_URL =
            "http://localhost:8081/api/readers/me/change-password";

    public List<Reader> filterReaders(
            String fullName,
            String email,
            String status,
            String gender) throws Exception {

        StringBuilder url = new StringBuilder(BASE_FILTER_URL);
        StringBuilder query = new StringBuilder();

        if (fullName != null && !fullName.isEmpty()) query.append("fullName=").append(URLEncoder.encode(fullName, "UTF-8")).append("&");
        if (email != null && !email.isEmpty()) query.append("email=").append(URLEncoder.encode(email, "UTF-8")).append("&");
        if (status != null && !status.isEmpty()) query.append("status=").append(status).append("&");
        if (gender != null && !gender.isEmpty()) query.append("gender=").append(gender);
        if (query.length() > 0) {
            if (query.charAt(query.length() - 1) == '&') {
                query.deleteCharAt(query.length() - 1);
            }
            url.append("?").append(query);
        }

        return get(url.toString(), new TypeReference<List<Reader>>() {});
    }

    public ApiResponse<Reader> registerReader(RegisterRequest request)
            throws Exception {

        return post(
                REGISTER_URL,
                request,
                new TypeReference<ApiResponse<Reader>>() {}
        );
    }

    public ApiResponse<Reader> getMyReaderInfo() throws Exception {
        return getMyInfo(
                ME_URL,
                new TypeReference<ApiResponse<Reader>>() {}
        );
    }

    public ApiResponse<Void> changeMyPassword(
            String oldPassword,
            String newPassword) throws Exception {

        return super.changeMyPassword(
                CHANGE_PASSWORD_URL,
                oldPassword,
                newPassword
        );
    }
}
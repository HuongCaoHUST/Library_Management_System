package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.model.Librarian;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class LibrarianApiService {
    private static final String BASE_URL = "http://localhost:8081/api/librarians/filter";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public List<Librarian> filterLibrarians(String fullName, String email, String status, String gender) throws Exception {
        String url = BASE_URL;
        StringBuilder query = new StringBuilder();
        if (fullName != null && !fullName.isEmpty()) query.append("fullName=").append(URLEncoder.encode(fullName, "UTF-8")).append("&");
        if (email != null && !email.isEmpty()) query.append("email=").append(URLEncoder.encode(email, "UTF-8")).append("&");
        if (status != null && !status.isEmpty()) query.append("status=").append(status).append("&");
        if (gender != null && !gender.isEmpty()) query.append("gender=").append(gender);

        if (query.length() > 0) {
            if (query.charAt(query.length() - 1) == '&') {
                query.deleteCharAt(query.length() - 1);
            }
            url += "?" + query;
        }

        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println(response.body());
            return mapper.readValue(response.body(), new TypeReference<List<Librarian>>() {});
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }

    public ApiResponse<Librarian> registerLibrarian(RegisterRequest requestDto) throws Exception {
        String url = "http://localhost:8081/api/librarians/register";

        String jsonBody = mapper.writeValueAsString(requestDto);
        System.out.println(jsonBody);

        String token = UserSession.getInstance().getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<Librarian>>() {}
        );
    }

    public ApiResponse<Librarian> getMyLibrarianInfo() throws Exception {
        String url = "http://localhost:8081/api/librarians/me";

        String token = UserSession.getInstance().getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<Librarian>>() {}
        );
    }

    public ApiResponse<Void> changeMyPassword(String oldPassword, String newPassword) throws Exception {
        String url = "http://localhost:8081/api/librarians/me/change-password";

        ChangePasswordRequest requestDto = new ChangePasswordRequest();
        requestDto.setOldPassword(oldPassword);
        requestDto.setNewPassword(newPassword);

        String jsonBody = mapper.writeValueAsString(requestDto);
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<Void>>() {}
        );
    }
}
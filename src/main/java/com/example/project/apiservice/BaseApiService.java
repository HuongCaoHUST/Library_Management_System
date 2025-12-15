package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseApiService {

    protected final HttpClient client = HttpClient.newHttpClient();

    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected String getToken() {
        return UserSession.getInstance().getToken();
    }

    protected <T> ApiResponse<T> getMyInfo(String url, TypeReference<ApiResponse<T>> type)
            throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getToken())
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), type);
    }

    protected <T> T get(String url, TypeReference<T> type) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + getToken())
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), type);
    }

    protected <T> T post(String url, Object body, TypeReference<T> type) throws Exception {
        String json = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), type);
    }

    protected ApiResponse<Void> changeMyPassword(
            String url,
            String oldPassword,
            String newPassword) throws Exception {

        ChangePasswordRequest requestDto = new ChangePasswordRequest();
        requestDto.setOldPassword(oldPassword);
        requestDto.setNewPassword(newPassword);

        return post(
                url,
                requestDto,
                new TypeReference<ApiResponse<Void>>() {}
        );
    }
}

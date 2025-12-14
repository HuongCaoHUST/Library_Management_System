package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.model.Supplier;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupplierApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public ApiResponse<Supplier> addSupplier (SupplierRequest requestDto) throws Exception {
        String url = "http://localhost:8081/api/suppliers/add";

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
                new TypeReference<ApiResponse<Supplier>>() {}
        );
    }
}
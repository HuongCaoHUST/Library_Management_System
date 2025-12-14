package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.model.Document;
import com.example.project.model.Supplier;
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

public class SupplierApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public List<Supplier> filterSuppliers(String supplierName, String phoneNumber) throws Exception {
        String url = "http://localhost:8081/api/suppliers/filter";
        StringBuilder query = new StringBuilder();
        if (supplierName != null && !supplierName.isEmpty()) query.append("title=").append(URLEncoder.encode(supplierName, "UTF-8")).append("&");
        if (phoneNumber != null && !phoneNumber.isEmpty()) query.append("author=").append(URLEncoder.encode(phoneNumber, "UTF-8")).append("&");

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
            return mapper.readValue(response.body(), new TypeReference<List<Supplier>>() {});
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }

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
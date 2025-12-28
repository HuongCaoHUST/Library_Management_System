package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.PermissionAddToRoleRequest;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PermissionApiService extends BaseApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String ADD_URL =
            "http://14.225.254.18/api/permissions/add/add_to_role";

    public List<PermissionRequest> getPermissionList() throws Exception {
        String url = "http://14.225.254.18/api/permissions/list";
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(
                response.body(),
                new TypeReference<List<PermissionRequest>>() {}
        );
    }

    public ApiResponse<PermissionRequest> addPermission (PermissionRequest request)
            throws Exception {
        return post(
                ADD_URL,
                request,
                new TypeReference<ApiResponse<PermissionRequest>>() {}
        );
    }

    public ApiResponse<PermissionRequest> addPermissionToRole (PermissionAddToRoleRequest request)
            throws Exception {
        return post(
                ADD_URL,
                request,
                new TypeReference<ApiResponse<PermissionRequest>>() {}
        );
    }
}
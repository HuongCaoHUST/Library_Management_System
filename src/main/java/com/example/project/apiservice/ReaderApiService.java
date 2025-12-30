package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.model.Reader;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ReaderApiService extends BaseApiService {
    private static final String BASE_FILTER_URL =
            "http://14.225.254.18/api/readers/filter";

    private static final String REGISTER_URL =
            "http://14.225.254.18/api/readers/register";

    private static final String ME_URL =
            "http://14.225.254.18/api/readers/me";

    private static final String CHANGE_PASSWORD_URL =
            "http://14.225.254.18/api/readers/me/change-password";

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

    public ApiResponse<Reader> registerReader(RegisterRequest requestDto) throws Exception {
        String url = "http://14.225.254.18/api/readers/register";

        String jsonBody = mapper.writeValueAsString(requestDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<Reader>>() {}
        );
    }

    public ApiResponse<Reader> registerReaderWithAvatar(RegisterRequest requestDto, File avatarFile) throws Exception {
        String url = "http://14.225.254.18/api/readers/register-with-avatar";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        if (avatarFile != null) {
            body.add("file", new FileSystemResource(avatarFile));
        }

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> dataPart = new HttpEntity<>(requestDto, jsonHeaders);
        body.add("data", dataPart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(UserSession.getInstance().getToken());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse<Reader>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<ApiResponse<Reader>>() {}
        );

        return response.getBody();
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
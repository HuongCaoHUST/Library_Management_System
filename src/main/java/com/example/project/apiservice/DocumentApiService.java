package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.model.Document;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DocumentApiService {
    private static final String BASE_URL = "http://14.225.254.18/api/documents/filter";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public List<Document> filterDocuments(String title, String author, String publisher, String documentType, Integer publicationYear) throws Exception {
        String url = BASE_URL;
        StringBuilder query = new StringBuilder();
        if (title != null && !title.isEmpty()) query.append("title=").append(URLEncoder.encode(title, "UTF-8")).append("&");
        if (author != null && !author.isEmpty()) query.append("author=").append(URLEncoder.encode(author, "UTF-8")).append("&");
        if (publisher != null && !publisher.isEmpty()) query.append("publisher=").append(publisher).append("&");
        if (documentType != null && !documentType.isEmpty()) query.append("documentType=").append(URLEncoder.encode(documentType, "UTF-8")).append("&");
        if (publicationYear != null) query.append("publicationYear=").append(publicationYear);

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
            return mapper.readValue(response.body(), new TypeReference<List<Document>>() {});
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }

    public ApiResponse<Document> addDocument(DocumentRequest requestDto) throws Exception {
        String url = "http://14.225.254.18/api/documents/add";

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
                new TypeReference<ApiResponse<Document>>() {}
        );
    }

    public ApiResponse<String> uploadDocumentCover(Long documentId, File imageFile) {

        String url = "http://14.225.254.18/api/documents/" + documentId + "/cover";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(imageFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(UserSession.getInstance().getToken());

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse<String>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<ApiResponse<String>>() {}
                );

        return response.getBody();
    }

}
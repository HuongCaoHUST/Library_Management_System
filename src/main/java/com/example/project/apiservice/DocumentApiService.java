package com.example.project.apiservice;

import com.example.project.model.Document;
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

public class DocumentApiService {
    private static final String BASE_URL = "http://localhost:8081/api/documents/filter";
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<Document>>() {});
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }
}
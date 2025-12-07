package com.example.project.apiservice;

import com.example.project.model.Grn;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GrnApiService {
    private static final String BASE_URL = "http://localhost:8081/api/grns";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Lấy tất cả phiếu nhập kho
     */
    public List<Grn> getAllGrns() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<Grn>>() {});
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }

    /**
     * Tìm phiếu nhập kho theo receiptId
     */
    public Grn getGrnByReceiptId(String receiptId) throws Exception {
        String url = BASE_URL + "/" + URLEncoder.encode(receiptId, "UTF-8");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Grn.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new RuntimeException("Lỗi API: " + response.statusCode());
        }
    }

    /**
     * Xóa phiếu nhập kho theo receiptId
     */
    public boolean deleteGrnByReceiptId(String receiptId) throws Exception {
        String url = BASE_URL + "/" + URLEncoder.encode(receiptId, "UTF-8");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 204; // 204 No Content = success
    }

    /**
     * Tìm kiếm phiếu nhập kho theo receiptId (filter)
     */
    public List<Grn> searchGrnsByReceiptId(String receiptId) throws Exception {
        List<Grn> allGrns = getAllGrns();

        if (receiptId == null || receiptId.trim().isEmpty()) {
            return allGrns;
        }

        String keyword = receiptId.trim().toLowerCase();
        return allGrns.stream()
                .filter(grn -> grn.getReceiptId().toLowerCase().contains(keyword))
                .toList();
    }
}
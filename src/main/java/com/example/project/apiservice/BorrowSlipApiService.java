package com.example.project.apiservice;

import com.example.project.model.BorrowItem;
import com.example.project.dto.request.BorrowSlipDetailRequest;
import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.security.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowSlipApiService {

    private static final String API_URL =
            "http://14.225.254.18/api/borrow_slips/add";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BorrowSlipApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // LocalDate
    }

    public void createBorrowSlip(
            Long readerId,
            LocalDate dueDate,
            List<BorrowItem> cartItems
    ) throws Exception {

        BorrowSlipRequest request = new BorrowSlipRequest();
        request.setReaderId(readerId);
        request.setDueDate(dueDate);
        request.setDetails(mapDetails(cartItems));

        String jsonBody = objectMapper.writeValueAsString(request);
        String token = UserSession.getInstance().getToken();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response =
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException(
                    "Create borrow slip failed: " + response.body()
            );
        }
    }

    private List<BorrowSlipDetailRequest> mapDetails(List<BorrowItem> cartItems) {
        return cartItems.stream()
                .map(item -> {
                    BorrowSlipDetailRequest detail = new BorrowSlipDetailRequest();
                    detail.setDocumentId(item.getDocument().getDocumentId());
                    detail.setQuantity(item.getQuantity());
                    return detail;
                })
                .collect(Collectors.toList());
    }
}

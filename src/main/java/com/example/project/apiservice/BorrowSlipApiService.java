package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.BorrowSlipResponse;
import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.request.RoleRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.util.List;

public class BorrowSlipApiService extends BaseApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String ADD_URL =
            "http://14.225.254.18/api/borrow_slips/add";
    private static final String MY_BORROW_SLIPS_URL =
            "http://14.225.254.18/api/borrow_slips/my_borrow_slips";

    public ApiResponse<BorrowSlipResponse> createBorrowSlip(BorrowSlipRequest request)
            throws Exception {
        return post(ADD_URL, request, new TypeReference<ApiResponse<BorrowSlipResponse>>() {}
        );
    }

    public ApiResponse<List<BorrowSlipResponse>> getMyBorrowSlips() throws Exception {
        return get(MY_BORROW_SLIPS_URL, new TypeReference<ApiResponse<List<BorrowSlipResponse>>>() {});
    }
}
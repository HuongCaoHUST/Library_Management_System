package com.example.project.apiservice;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.GRNResponse; // Assuming GRNResponse is in dto package
import com.example.project.dto.request.GRNRequest; // Assuming GRNRequest is in dto.request package
import com.fasterxml.jackson.core.type.TypeReference;

public class GRNApiService extends BaseApiService {

    public GRNApiService() {
        super();
    }

    public ApiResponse<GRNResponse> createGrn(GRNRequest request) {
        String jsonRequest = gson.toJson(request);
        return post("/grns/add", jsonRequest, new TypeReference<ApiResponse<GRNResponse>>() {});
    }
}

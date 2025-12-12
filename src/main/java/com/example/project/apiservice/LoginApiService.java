package com.example.project.apiservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginApiService {

    private static final String LOGIN_URL = "http://localhost:8081/auth/login";
    private final ObjectMapper mapper = new ObjectMapper();

    public static class LoginResponse {
        public boolean success;
        public String token;
        public String fullName;
        public String role;
        public String errorMessage;
    }

    public LoginResponse login(String username, String password) {
        LoginResponse result = new LoginResponse();
        HttpURLConnection conn = null;

        try {
            URL url = new URL(LOGIN_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // JSON Send
            String body = """
                    {
                      "username": "%s",
                      "password": "%s"
                    }
                    """.formatted(username, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int status = conn.getResponseCode();

            // OK
            if (status == 200) {
                String jsonResp = readStream(conn.getInputStream());

                JsonNode node = mapper.readTree(jsonResp);

                result.success = true;
                result.token = node.get("token").asText();
                result.fullName = node.get("fullName").asText();
                result.role = node.get("role").asText();

                return result;
            }

            // Password incorrect
            if (status == 401) {
                result.success = false;
                result.errorMessage = "Sai username hoặc password!";
                return result;
            }

            result.success = false;
            result.errorMessage = "Lỗi server: HTTP " + status;

        } catch (Exception e) {
            result.success = false;
            result.errorMessage = "Không kết nối được tới backend!";
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }
}

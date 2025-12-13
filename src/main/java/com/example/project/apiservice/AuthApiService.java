package com.example.project.apiservice;

import com.example.project.dto.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AuthApiService {

    private static final String LOGIN_URL = "http://localhost:8081/auth/login";
    private final ObjectMapper mapper = new ObjectMapper();

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

                result.setSuccess(true);
                result.setToken(node.get("token").asText());
                result.setFullName(node.get("fullName").asText());
                result.setRole(node.get("role").asText());

                Set<String> permissions = new HashSet<>();
                if (node.has("permissions")) {
                    for (JsonNode p : node.get("permissions")) {
                        permissions.add(p.asText());
                    }
                }
                result.setPermissions(permissions);

                return result;
            }

            // Password incorrect
            if (status == 401) {
                result.setSuccess(false);
                result.setErrorMessage("Sai username hoặc password!");
                return result;
            }

            result.setSuccess(false);
            result.setErrorMessage("Lỗi server: HTTP " + status);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Không kết nối được tới backend!");
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

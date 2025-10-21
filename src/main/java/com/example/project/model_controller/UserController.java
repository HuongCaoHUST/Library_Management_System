package com.example.project.model_controller;

import com.example.project.models.User;
import java.security.SecureRandom;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserController {
    private final List<User> users;
    private final String FILE_PATH = "./data/users.txt";
    private static final String ACCOUNT_FILE = "./data/account.txt";
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public UserController() {
        users = new ArrayList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 12) continue; // bỏ qua dòng không đủ trường
                users.add(new User(parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ADD
    public void addUser(User user) {
        users.add(user);
        saveToFile(user);
        saveToFileAccount(user);
    }

    private void saveToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String line = String.join(",",
                    user.getStudentId(),
                    user.getFullName(),
                    user.getGender(),
                    user.getBirthDate(),
                    user.getPhoneNumber(),
                    user.getEmail(),
                    user.getIdCardNumber(),
                    user.getPlaceOfBirth(),
                    user.getIssuedPlace(),
                    user.getMajor(),
                    user.getWorkPlace(),
                    user.getAddress()
            );
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFileAccount(User user) {
        String username = user.getStudentId();
        String password = generateRandomPassword(8);
        String role = "ban_doc";
        String status = Math.random() < 0.5 ? "ready" : "inactive";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE, true))) {
            String line = String.join(",", username, password, role, status);
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Random password
    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    // REMOVE
    public boolean deleteUser(String studentId) {
        return users.removeIf(u -> u.getStudentId().equals(studentId));
    }

    // UPDATE
    public boolean updateUser(String studentId, User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getStudentId().equals(studentId)) {
                users.set(i, updatedUser);
                return true;
            }
        }
        return false;
    }

    // FIND BY MSSV
    public User getUserById(String studentId) {
        Optional<User> user = users.stream()
                .filter(u -> u.getStudentId().equals(studentId))
                .findFirst();
        return user.orElse(null);
    }

    // GET
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // FIND BY NAME
    public List<User> searchByName(String keyword) {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if (u.getFullName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(u);
            }
        }
        return result;
    }
}

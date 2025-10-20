package com.example.project.model_controller;

import com.example.project.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserController {
    private final List<User> users;
    private final String FILE_PATH = "./data/users.txt";

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

package com.example.project.model_controller;

import com.example.project.models.Doc;
import com.example.project.models.User;

import java.security.SecureRandom;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.List;

public class DocController {
    private final List<Doc> docs;
    private final String FILE_PATH = "./data/docs.txt";

    public DocController() {
        docs = new ArrayList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",",-1);
                if (parts.length < 11) continue; // bỏ qua dòng không đủ trường
                docs.add(new Doc(parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDoc(Doc doc) {
        docs.add(doc);
        saveToFile(doc);
    }

    private void saveToFile(Doc doc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String line = String.join(",",
                    doc.getDocId(),
                    doc.getTitle(),
                    doc.getAuthor(),
                    doc.getPublisher(),
                    doc.getPubYear(),
                    doc.getCategory(),
                    doc.getDocType(),
                    doc.getShelfLoc() == null ? "" : doc.getShelfLoc(),
                    doc.getAccessUrl() == null ? "" : doc.getAccessUrl(),
                    doc.getStatus() == null ? "" : doc.getStatus()
            );
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // FIND by ID (trả về 1 Doc hoặc null nếu không thấy)
    public Doc findDocId(String id) {
        if (id == null) return null;
        return docs.stream()
                .filter(d -> id.equals(d.getDocId()))
                .findFirst()
                .orElse(null);
    }

    // FIND by Name (trả về danh sách Doc có title chứa chuỗi tìm kiếm, không phân biệt hoa/thường)
    public List<Doc> findDocName(String namePart) {
        List<Doc> result = new ArrayList<>();
        if (namePart == null) return result;

        String needle = namePart.trim().toLowerCase();
        if (needle.isEmpty()) return result;

        for (Doc d : docs) {
            String title = d.getTitle();
            if (title != null && title.toLowerCase().contains(needle)) {
                result.add(d);
            }
        }
        return result;
    }
}

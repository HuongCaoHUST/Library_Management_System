package com.example.project.models;

public class Doc {
    private final String docId;     // Mã tài liệu
    private final String title;     // Tên tài liệu
    private final String author;    // Tác giả
    private final String publisher; // Nhà xuất bản
    private final String pubYear;   // Năm xuất bản
    private final String category;  // Thể loại (khoa học, văn học, v.v.)
    private final String shelfLoc;  // Vị trí trên kệ
    private final String docType;   // Loại tài liệu (print/digital)
    private final String accessUrl; // Liên kết truy cập (nếu là tài nguyên số)
    private final String status;    // Tình trạng (còn/hết)

    public Doc(String[] parts) {
        this.docId     = parts[0].trim();
        this.title     = parts[1].trim();
        this.author    = parts[2].trim();
        this.publisher = parts[3].trim();
        this.pubYear   = parts[4].trim();
        this.category  = parts[6].trim();
        this.docType   = parts[7].trim();
        this.shelfLoc  = parts[8].trim();
        this.accessUrl = parts[9].trim();
        this.status    = parts[10].trim();
    }

    // getters
    public String getDocId() { return docId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getPubYear() { return pubYear; }
    public String getCategory() { return category; }
    public String getShelfLoc() { return shelfLoc; }
    public String getDocType() { return docType; }
    public String getAccessUrl() { return accessUrl; }
    public String getStatus() { return status; }
}
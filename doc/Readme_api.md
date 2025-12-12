# Reader API
**Library Management System** – Spring Boot + JavaFX

**Base URL:** `http://localhost:8081/api/readers`  
**Port can be changed in `application.properties` → `server.port=8081`**

---

# Reader API Usage Guide
## 0. Check Active API
GET: http://localhost:8081/api/readers/test

## 1. Get all readers
GET: http://localhost:8081/api/readers/filter

## 2. Search by full name (case-insensitive)
GET: http://localhost:8081/api/readers/filter?fullName=nguyen

## 3. Search by email
GET: http://localhost:8081/api/readers/filter?email=gmail

## 4. Search by status
GET: http://localhost:8081/api/readers/filter?status=APPROVED

## 5. Search by gender
GET: http://localhost:8081/api/readers/filter?gender=MALE

## 6. Combine multiple conditions
GET: http://localhost:8081/api/readers/filter?fullName=an&status=APPROVED&gender=MALE


# Document API Usage Guide
## 0. Check Document API
GET: http://localhost:8081/api/documents/test

## 1. Get All Document API
GET: http://localhost:8081/api/documents/filter

## 1. Post one Document API
POST: http://localhost:8081/api/documents
### Response Example
```json
  {
    "title": "Lập trình Java Cơ Bản",
    "author": "Nguyễn Văn A",
    "publisher": "NXB Giáo Dục",
    "publicationYear": 2023,
    "ddcNumber": "005.133",
    "cutterCode": "N576",
    "availableCopies": 10,
    "borrowedCopies": 0,
    "coverPrice": 185000.0,
    "classificationNumber": "JAVA-001",
    "category": "Công nghệ thông tin",
    "shelfLocation": "Kệ A3 - Tầng 2",
    "documentType": "Sách giáo trình",
    "accessLink": "http://library.example.com/java-book",
    "status": "AVAILABLE"
  }
```
## 2. Post bulk Document API
POST: http://localhost:8081/api/documents/bulk
### Response Example
```json
  {
  "documents": [
    {
      "title": "Java Cơ Bản",
      "author": "Nguyễn Văn A",
      "availableCopies": 10,
      "documentType": "Sách"
    },
    {
      "title": "Spring Boot Từ A-Z",
      "author": "Trần Thị B",
      "availableCopies": 5,
      "publisher": "NXB Kim Đồng",
      "publicationYear": 2024
    },
    {
      "title": "Python Cho Người Mới",
      "author": "Lê Văn C",
      "availableCopies": 8,
      "status": "AVAILABLE"
    }
  ]
}
```

## 3. PUT Document API
PUT: http://localhost:8081/api/documents/[DOCUMENT_ID]
### Response Example
```json
{
  "title": "Java Nâng Cao - PUT",
  "author": "Nguyễn Văn A",
  "availableCopies": 20,
  "status": null
}
```

## 4. PATCH Document API
PATCH: http://localhost:8081/api/documents/[DOCUMENT_ID]
### Response Example
```json
  {
  "availableCopies": 15,
  "status": "AVAILABLE"
}
```

## 5. Delete one Document API
DELETE: http://localhost:8081/api/documents/[DOCUMENT_ID]
### Response Example
```http request
  http://localhost:8080/api/documents/1
```

## 6. Delete bulk Document API
DELETE: http://localhost:8081/api/documents
### Response Example
```json
  {
    "documentIds": [2, 3, 999]
  }
```

# LOGIN API
## 1. Post login API
POST: http://localhost:8081/auth/login
### Body Example
```json
  {
  "username": "admin",
  "password": "123"
}
```






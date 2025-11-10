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

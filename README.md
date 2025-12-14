# 📚 Library Management System (Spring Boot + JavaFX + MySQL)

## 🧱 Cấu trúc thư mục dự án

```
LibraryManagementSystem/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │   └─ com/example/project/
│  │  │       ├─ HelloApplication.java              <-- Entry chính (Spring Boot + JavaFX)
│  │  │       │
│  │  │       ├─ config/
│  │  │       │   └─ WebConfig.java                 <-- Cấu hình CORS, Bean,...
│  │  │       │
│  │  │       ├─ model/
│  │  │       │   ├─ Book.java
│  │  │       │   ├─ Author.java
│  │  │       │   ├─ Category.java
│  │  │       │   ├─ Member.java
│  │  │       │   └─ Loan.java
│  │  │       │
│  │  │       ├─ supplierRepository/
│  │  │       │   ├─ BookRepository.java
│  │  │       │   ├─ AuthorRepository.java
│  │  │       │   ├─ CategoryRepository.java
│  │  │       │   ├─ MemberRepository.java
│  │  │       │   └─ LoanRepository.java
│  │  │       │
│  │  │       ├─ service/
│  │  │       │   ├─ BookService.java
│  │  │       │   ├─ MemberService.java
│  │  │       │   └─ LoanService.java
│  │  │       │
│  │  │       ├─ controller/
│  │  │       │   ├─ BookController.java
│  │  │       │   ├─ MemberController.java
│  │  │       │   └─ LoanController.java
│  │  │       │
│  │  │       ├─ javafx/
│  │  │       │   ├─ controller/
│  │  │       │   │   ├─ MainController.java
│  │  │       │   │   ├─ BookControllerFX.java
│  │  │       │   │   ├─ MemberControllerFX.java
│  │  │       │   │   └─ LoanControllerFX.java
│  │  │       │   └─ service/
│  │  │       │       └─ ApiClient.java             <-- Gọi REST API tới backend
│  │  │       │
│  │  │       └─ util/
│  │  │           └─ DateUtils.java
│  │  │
│  │  ├─ resources/
│  │  │   ├─ application.yml                        <-- Cấu hình Spring Boot (MySQL,...)
│  │  │   ├─ fxml/
│  │  │   │   ├─ main.fxml
│  │  │   │   ├─ books.fxml
│  │  │   │   ├─ members.fxml
│  │  │   │   └─ loans.fxml
│  │  │   ├─ static/                                <-- (tài nguyên web, nếu có)
│  │  │   └─ templates/                             <-- (dành cho Thymeleaf, nếu dùng)
│  │  │
│  └─ test/
│      └─ java/
│          └─ com/example/project/
│              ├─ BookServiceTest.java
│              └─ LoanServiceTest.java
│
├─ README.md
└─ docker-compose.yml                              <-- chạy MySQL (tuỳ chọn)
```

## ⚙️ Mô tả nhanh

| Thư mục / file            | Chức năng                                                    |
| ------------------------- | ------------------------------------------------------------ |
| **HelloApplication.java** | Điểm khởi đầu ứng dụng (Spring Boot + JavaFX).               |
| **model/**                | Các lớp thực thể (Book, Member, Loan, Author, Category).     |
| **supplierRepository/**           | Các interface giao tiếp DB dùng Spring Data JPA.             |
| **service/**              | Chứa logic nghiệp vụ chính (mượn/trả, kiểm tra quá hạn,...). |
| **controller/**           | REST API endpoints cho backend.                              |
| **javafx/**               | Mã giao diện JavaFX (FXML + controller + gọi API).           |
| **resources/fxml/**       | File giao diện JavaFX.                                       |
| **application.yml**       | Cấu hình DB, port, Hibernate,...                             |
| **docker-compose.yml**    | Chạy MySQL nhanh qua Docker.                                 |

## 🚀 Hướng dẫn chạy nhanh

1. **Khởi chạy MySQL** (local hoặc Docker):

   ```bash
   docker-compose up -d
   ```

2. **Chạy backend Spring Boot:**

   ```bash
   mvn spring-boot:run
   ```

3. **Chạy JavaFX UI** từ IntelliJ (class `HelloApplication`).

4. Giao diện JavaFX sẽ giao tiếp qua REST API `http://localhost:8080/api/...`.

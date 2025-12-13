package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.LibrarianRepository;
import com.example.project.security.Role;
import com.example.project.specification.LibrarianSpecification;
import com.example.project.util.PasswordUtils;
import com.example.project.util.SendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository repository;
    private final SendEmail sendEmail;

    public List<Librarian> findAll() {
        return repository.findAll();
    }

    public Optional<Librarian> findById(Long id) {
        return repository.findById(id);
    }
    public Optional<Librarian> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Librarian save(Librarian librarian) {
        return repository.save(librarian);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Librarian> filterLibrarians(String fullName, String email, String status, String gender) {
        Specification<Librarian> spec = Specification
                .where(LibrarianSpecification.hasFullName(fullName))
                .and(LibrarianSpecification.hasEmail(email))
                .and(LibrarianSpecification.hasStatus(status))
                .and(LibrarianSpecification.hasGender(gender));

        return repository.findAll(spec);
    }

    public Librarian registerLibrarian(Librarian inputLibrarian) {
        String email = inputLibrarian.getEmail().trim().toLowerCase();
        String username = email;
        String rawPassword = PasswordUtils.generateRandomPassword(8);
        String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);

        Optional<Librarian> admin = findById(1L);
        Librarian approvingLibrarian = admin.get();
        System.out.println("Found librarian ID: " + approvingLibrarian.getUserId());
        System.out.println("Librarian name: " + approvingLibrarian.getFullName());

        Librarian librarian = Librarian.builder()
                .fullName(inputLibrarian.getFullName())
                .gender(inputLibrarian.getGender())
                .birthDate(inputLibrarian.getBirthDate())
                .phoneNumber(inputLibrarian.getPhoneNumber())
                .email(inputLibrarian.getEmail())
                .idCardNumber(inputLibrarian.getIdCardNumber())
                .placeOfBirth(inputLibrarian.getPlaceOfBirth())
                .issuedPlace(inputLibrarian.getIssuedPlace())
                .major(inputLibrarian.getMajor())
                .workPlace(inputLibrarian.getWorkPlace())
                .address(inputLibrarian.getAddress())
                .username(username)
                .password(encryptedPassword)
                .registrationDate(LocalDateTime.now())
                .status("APPROVED")
                .approvedBy(approvingLibrarian)
                .depositAmount(BigDecimal.ZERO)
                .role(Role.LIBRARIAN)
                .build();
        sendEmailSuccess(librarian, rawPassword);
        return repository.save(librarian);
    }

    public void sendEmailSuccess(Librarian librarian, String rawPassword) {
        String subject = "[THỦ THƯ] Tài khoản thư viện của bạn đã được phê duyệt";
        String body = "Xin chào " + librarian.getFullName() + ",\n\n"
                + "Tài khoản của bạn đã được phê duyệt thành công!\n"
                + "Tên đăng nhập: " + librarian.getUsername() + "\n"
                + "Mật khẩu: " + rawPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n"
                + "Thân mến,\nPhòng Thư viện";

        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByIdCardNumber(String idCardNumber) {
        return repository.existsByIdCardNumber(idCardNumber);
    }
}

package com.example.project.service;

import com.example.project.model.Reader;
import com.example.project.security.Role;
import com.example.project.repository.ReaderRepository;
import com.example.project.specification.ReaderSpecification;
import com.example.project.util.PasswordUtils;
import com.example.project.util.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReaderService {
    private final ReaderRepository repository;

    public ReaderService(ReaderRepository repository) {
        this.repository = repository;
    }

    public List<Reader> findAll() {
        return repository.findAll();
    }

    public Optional<Reader> findById(Long id) {
        return repository.findById(id);
    }

    public Reader save(Reader reader) {
        return repository.save(reader);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private SendEmail sendEmail;

    public Reader registerReader(Reader inputReader) {
        String email = inputReader.getEmail().trim().toLowerCase();
        String username = email;
        String rawPassword = PasswordUtils.generateRandomPassword(8);
        String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);

        Reader reader = Reader.builder()
                .fullName(inputReader.getFullName())
                .gender(inputReader.getGender())
                .birthDate(inputReader.getBirthDate())
                .phoneNumber(inputReader.getPhoneNumber())
                .email(inputReader.getEmail())
                .idCardNumber(inputReader.getIdCardNumber())
                .placeOfBirth(inputReader.getPlaceOfBirth())
                .issuedPlace(inputReader.getIssuedPlace())
                .major(inputReader.getMajor())
                .workPlace(inputReader.getWorkPlace())
                .address(inputReader.getAddress())
                .username(username)
                .password(encryptedPassword)
                .registrationDate(LocalDateTime.now())
                .status("APPROVED")
//                .approvedBy()
                .depositAmount(BigDecimal.ZERO)
                .role(Role.READER)
                .build();
        sendEmailSuccess(reader, rawPassword);
        return repository.save(reader);
    }

    public void sendEmailSuccess(Reader reader, String rawPassword) {
        String subject = "Tài khoản thư viện của bạn đã được phê duyệt";
        String body = "Xin chào " + reader.getFullName() + ",\n\n"
                + "Tài khoản của bạn đã được phê duyệt thành công!\n"
                + "Tên đăng nhập: " + reader.getUsername() + "\n"
                + "Mật khẩu: " + rawPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi sử dụng lần đầu.\n\n"
                + "Thân mến,\nPhòng Thư viện";

        sendEmail.sendMail("huongcao.seee@gmail.com", subject, body);
    }

//    public List<Reader> getPendingReaders() {
//        return readerRepository.findByStatus("PENDING");
//    }
//
//    public List<Reader> getApprovedReaders() {
//        return readerRepository.findByStatus("APPROVED");
//    }

    public List<Reader> filterReaders(String fullName, String email, String status, String gender) {
        Specification<Reader> spec = Specification
                .where(ReaderSpecification.hasFullName(fullName))
                .and(ReaderSpecification.hasEmail(email))
                .and(ReaderSpecification.hasStatus(status))
                .and(ReaderSpecification.hasGender(gender));

        return repository.findAll(spec);
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

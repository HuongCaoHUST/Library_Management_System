package com.example.project.service;

import com.example.project.dto.request.LibrarianRequest;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.Librarian;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.model.Role;
import com.example.project.specification.LibrarianSpecification;
import com.example.project.util.PasswordUtils;
import com.example.project.util.SendEmail;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final RoleRepository roleRepository;

    private final SendEmail sendEmail;
    private final LibrarianMapper mapper;

    public List<Librarian> findAll() {
        return librarianRepository.findAll();
    }

    public Optional<Librarian> findById(Long id) {
        return librarianRepository.findById(id);
    }
    public Optional<Librarian> findByUsername(String username) {
        return librarianRepository.findByUsername(username);
    }

    public Librarian save(Librarian librarian) {
        return librarianRepository.save(librarian);
    }

    public boolean existsByUsername(String username) {
        return librarianRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return librarianRepository.existsByEmail(email);
    }

    public boolean existsByIdCardNumber(String idCardNumber) {
        return librarianRepository.existsByIdCardNumber(idCardNumber);
    }

//    public void delete(Long id) {
//        librarianRepository.deleteById(id);
//    }

    public List<Librarian> filterLibrarians(String fullName, String email, String status, String gender) {
        Specification<Librarian> spec = Specification
                .where(LibrarianSpecification.hasFullName(fullName))
                .and(LibrarianSpecification.hasEmail(email))
                .and(LibrarianSpecification.hasStatus(status))
                .and(LibrarianSpecification.hasGender(gender));

        return librarianRepository.findAll(spec);
    }

    public Librarian registerLibrarian(Librarian inputLibrarian) {
        String email = inputLibrarian.getEmail().trim().toLowerCase();
        String username = email;
        String rawPassword = PasswordUtils.generateRandomPassword(8);
        String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);

        Optional<Librarian> admin = findById(1L);
        Librarian approvingLibrarian = admin.get();
        Role librarianRole = roleRepository.findByName("LIBRARIAN").orElseThrow(() -> new RuntimeException("Role not found"));

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
                .role(librarianRole)
                .build();
        sendEmailSuccess(librarian, rawPassword);
        return librarianRepository.save(librarian);
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

    public Librarian updatePatch(Long id, LibrarianRequest request) {
        Librarian librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thủ thư"));
        mapper.patch(librarian, request);
        return librarianRepository.save(librarian);
    }

    public void delete(Long id) {
        if (!librarianRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy thủ thư");
        }
        librarianRepository.deleteById(id);
    }

    public ByteArrayInputStream exportLibrarianListToExcel() {
        try (
                InputStream templateStream =
                        getClass().getResourceAsStream("/templates/librarian_export_template.xlsx");
                Workbook workbook = new XSSFWorkbook(templateStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Librarian> librarians = librarianRepository.findAll();

            int rowIndex = 1;
            for (Librarian librarian : librarians) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(librarian.getFullName());
                row.createCell(1).setCellValue(librarian.getGender());
                row.createCell(2).setCellValue(librarian.getBirthDate());
                row.createCell(3).setCellValue(librarian.getPlaceOfBirth());
                row.createCell(4).setCellValue(librarian.getIdCardNumber());
                row.createCell(5).setCellValue(librarian.getIssuedPlace());
                row.createCell(6).setCellValue(librarian.getMajor());
                row.createCell(7).setCellValue(librarian.getWorkPlace());
                row.createCell(8).setCellValue(librarian.getAddress());
                row.createCell(9).setCellValue(librarian.getPhoneNumber());
                row.createCell(10).setCellValue(librarian.getEmail());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Xuất Excel thất bại", e);
        }
    }
}

package com.example.project.service;

import com.example.project.dto.request.ReaderRequest;
import com.example.project.mapper.ReaderMapper;
import com.example.project.model.Document;
import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.model.Role;
import com.example.project.repository.ReaderRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.specification.ReaderSpecification;
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
public class ReaderService {

    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    public Reader save(Reader reader) {
        return readerRepository.save(reader);
    }

    private final ReaderRepository readerRepository;
    private final RoleRepository roleRepository;

    private final LibrarianService librarianService;
    private final SendEmail sendEmail;
    private final ReaderMapper mapper;

    public Optional<Reader> findByUsername(String username) {
        return readerRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return readerRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return readerRepository.existsByEmail(email);
    }

    public boolean existsByIdCardNumber(String idCardNumber) { return readerRepository.existsByIdCardNumber(idCardNumber);}

    public Reader registerReader(Reader inputReader) {
        String email = inputReader.getEmail().trim().toLowerCase();
        String username = email;
        String rawPassword = PasswordUtils.generateRandomPassword(8);
        String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);

        Optional<Librarian> librarian = librarianService.findById(2L);
        Librarian approvingLibrarian = librarian.get();
        System.out.println("Found librarian ID: " + approvingLibrarian.getUserId());
        System.out.println("Librarian name: " + approvingLibrarian.getFullName());

        Role readerRole = roleRepository.findByName("LIBRARIAN").orElseThrow(() -> new RuntimeException("Role not found"));


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
                .approvedBy(approvingLibrarian)
                .depositAmount(BigDecimal.ZERO)
                .role(readerRole)
                .build();
        sendEmailSuccess(reader, rawPassword);
        return readerRepository.save(reader);
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

        return readerRepository.findAll(spec);
    }

    public Reader updatePatch(Long id, ReaderRequest request) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bạn đọc"));
        mapper.patch(reader, request);
        return readerRepository.save(reader);
    }

    public void delete(Long id) {
        if (!readerRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy bạn đọc");
        }
        readerRepository.deleteById(id);
    }

    public ByteArrayInputStream exportReaderListToExcel() {
        try (
                InputStream templateStream =
                        getClass().getResourceAsStream("/templates/reader_export_template.xlsx");
                Workbook workbook = new XSSFWorkbook(templateStream);
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Reader> readers = readerRepository.findAll();

            int rowIndex = 1;
            for (Reader reader : readers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(reader.getFullName());
                row.createCell(1).setCellValue(reader.getGender());
                row.createCell(2).setCellValue(reader.getBirthDate());
                row.createCell(3).setCellValue(reader.getPlaceOfBirth());
                row.createCell(4).setCellValue(reader.getIdCardNumber());
                row.createCell(5).setCellValue(reader.getIssuedPlace());
                row.createCell(6).setCellValue(reader.getMajor());
                row.createCell(7).setCellValue(reader.getWorkPlace());
                row.createCell(8).setCellValue(reader.getAddress());
                row.createCell(9).setCellValue(reader.getPhoneNumber());
                row.createCell(10).setCellValue(reader.getEmail());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Xuất Excel thất bại", e);
        }
    }
}

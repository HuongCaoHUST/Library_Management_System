package com.example.project.service;

import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.UserResponse;
import com.example.project.mapper.ReaderMapper;
import com.example.project.model.*;
import com.example.project.repository.ReaderRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.specification.ReaderSpecification;
import com.example.project.util.PasswordUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public Reader findById(Long userId) {
        return readerRepository.findByUserId((userId));
    }

    private final ReaderRepository readerRepository;
    private final RoleRepository roleRepository;

    private final LibrarianService librarianService;
    private final EmailService emailService;
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

    public UserResponse registerReader(ReaderRequest request) {

        if (readerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        } else if (readerRepository.existsByIdCardNumber(request.getIdCardNumber())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String email = request.getEmail().trim().toLowerCase();
        String username = email;
        String rawPassword = PasswordUtils.generateRandomPassword(8);
        String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);

        Optional<Librarian> librarian = librarianService.findById(2L);
        Librarian approvingLibrarian = librarian.get();
        System.out.println("Found librarian ID: " + approvingLibrarian.getUserId());
        System.out.println("Librarian name: " + approvingLibrarian.getFullName());

        Role readerRole = roleRepository.findByName("LIBRARIAN").orElseThrow(() -> new RuntimeException("Role not found"));


        Reader reader = Reader.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .idCardNumber(request.getIdCardNumber())
                .placeOfBirth(request.getPlaceOfBirth())
                .issuedPlace(request.getIssuedPlace())
                .major(request.getMajor())
                .workPlace(request.getWorkPlace())
                .address(request.getAddress())
                .username(username)
                .password(encryptedPassword)
                .registrationDate(LocalDateTime.now())
                .status("APPROVED")
                .approvedBy(approvingLibrarian)
                .depositAmount(BigDecimal.ZERO)
                .role(readerRole)
                .build();
        emailService.sendReaderAccountApproved(reader, rawPassword);

        Reader saved = readerRepository.save(reader);
        return mapper.toResponse(saved);
    }

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

    @Transactional
    public void importReaderFromExcel(InputStream inputStream) throws Exception {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        List<Reader> readers = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Reader reader = new Reader();


            reader.setFullName(getCellString(row.getCell(0)));
            reader.setGender(getCellString(row.getCell(1)));
            String birthDateStr = getCellString(row.getCell(2));

            reader.setBirthDate(
                    birthDateStr == null || birthDateStr.isBlank()
                            ? null
                            : LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );

            reader.setPlaceOfBirth(getCellString(row.getCell(3)));
            reader.setIdCardNumber(getCellString(row.getCell(4)));
            reader.setIssuedPlace(getCellString(row.getCell(5)));
            reader.setMajor(getCellString(row.getCell(6)));
            reader.setWorkPlace(getCellString(row.getCell(7)));
            reader.setAddress(getCellString(row.getCell(8)));
            reader.setPhoneNumber(getCellString(row.getCell(9)));
            reader.setEmail(getCellString(row.getCell(10)));

            String email = reader.getEmail().trim().toLowerCase();
            String username = email;
            reader.setUsername(username);

            if (existsByUsername(reader.getUsername())) {
                throw new RuntimeException("Username đã tồn tại");
            }
            if (existsByEmail(reader.getEmail())) {
                throw new RuntimeException("Email đã tồn tại");
            }
            if (existsByIdCardNumber(reader.getIdCardNumber())) {
                throw new RuntimeException("CCCD đã tồn tại");
            }


            String rawPassword = PasswordUtils.generateRandomPassword(8);
            String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);
            reader.setPassword(encryptedPassword);

            Optional<Librarian> librarian = librarianService.findById(2L);
            Librarian approvingLibrarian = librarian.get();
            reader.setApprovedBy(approvingLibrarian);

            Role librarianRole = roleRepository.findByName("LIBRARIAN").orElseThrow(() -> new RuntimeException("Role LIBRARIAN not found"));
            reader.setRole(librarianRole);

            emailService.sendReaderAccountApproved(reader, rawPassword);

            readers.add(reader);
        }

        readerRepository.saveAll(readers);
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
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

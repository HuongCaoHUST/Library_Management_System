package com.example.project.service;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.LibrarianRequest;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.*;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.specification.LibrarianSpecification;
import com.example.project.util.PasswordUtils;
import com.example.project.util.SendEmail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
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
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

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
        emailService.sendLibrarianAccountApproved(librarian, rawPassword);
        return librarianRepository.save(librarian);
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

    @Transactional
    public void importLibrarianFromExcel(InputStream inputStream) throws Exception {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        List<Librarian> librarians = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Librarian librarian = new Librarian();


            librarian.setFullName(getCellString(row.getCell(0)));
            librarian.setGender(getCellString(row.getCell(1)));
            String birthDateStr = getCellString(row.getCell(2));

            librarian.setBirthDate(
                    birthDateStr == null || birthDateStr.isBlank()
                            ? null
                            : LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );

            librarian.setPlaceOfBirth(getCellString(row.getCell(3)));
            librarian.setIdCardNumber(getCellString(row.getCell(4)));
            librarian.setIssuedPlace(getCellString(row.getCell(5)));
            librarian.setMajor(getCellString(row.getCell(6)));
            librarian.setWorkPlace(getCellString(row.getCell(7)));
            librarian.setAddress(getCellString(row.getCell(8)));
            librarian.setPhoneNumber(getCellString(row.getCell(9)));
            librarian.setEmail(getCellString(row.getCell(10)));

            String email = librarian.getEmail().trim().toLowerCase();
            String username = email;
            librarian.setUsername(username);

            if (existsByUsername(librarian.getUsername())) {
                throw new RuntimeException("Username đã tồn tại");
            }
            if (existsByEmail(librarian.getEmail())) {
                throw new RuntimeException("Email đã tồn tại");
            }
            if (existsByIdCardNumber(librarian.getIdCardNumber())) {
                throw new RuntimeException("CCCD đã tồn tại");
            }


            String rawPassword = PasswordUtils.generateRandomPassword(8);
            String encryptedPassword = PasswordUtils.encryptPassword(rawPassword);
            librarian.setPassword(encryptedPassword);

            Optional<Librarian> admin = findById(1L);
            Librarian approvingLibrarian = admin.get();
            librarian.setApprovedBy(approvingLibrarian);

            Role librarianRole = roleRepository.findByName("LIBRARIAN").orElseThrow(() -> new RuntimeException("Role LIBRARIAN not found"));
            librarian.setRole(librarianRole);

            emailService.sendLibrarianAccountApproved(librarian, rawPassword);

            librarians.add(librarian);
        }

        librarianRepository.saveAll(librarians);
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

package com.example.project.service;

import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.UserResponse;
import com.example.project.mapper.ReaderMapper;
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

public interface ReaderService2 {
    void updateAvatar(Long readerId, String avatarUrl);
}

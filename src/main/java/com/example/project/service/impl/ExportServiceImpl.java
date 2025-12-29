package com.example.project.service.impl;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.ReaderRepository;
import com.example.project.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final LibrarianRepository librarianRepository;
    private final ReaderRepository readerRepository;

    @Override
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
                row.createCell(2).setCellValue(librarian.getBirthDate().toString());
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
            throw new RuntimeException("Xuất danh sách thủ thư thất bại", e);
        }
    }

    @Override
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
            throw new RuntimeException("Xuất danh sách độc giả thất bại", e);
        }
    }
}

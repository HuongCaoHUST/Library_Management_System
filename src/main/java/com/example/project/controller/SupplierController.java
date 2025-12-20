package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.dto.response.ReaderResponse;
import com.example.project.dto.response.SupplierResponse;
import com.example.project.mapper.SupplierMapper;
import com.example.project.model.Document;
import com.example.project.model.Reader;
import com.example.project.model.Supplier;
import com.example.project.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    private SupplierMapper mapper;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Suppliers Controller is working!";
    }

    @GetMapping("/filter")
    public List<Supplier> filterSuppliers(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String phoneNumber
    ) {
        return supplierService.filterSuppliers(supplierName, phoneNumber);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@RequestBody SupplierRequest request) {
        try {
            Supplier supplier = supplierService.create(request);
            SupplierResponse response = mapper.toResponse(supplier);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm nhà cung cấp thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> patchLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        Supplier updated = supplierService.updatePatch(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công!", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLibrarian(@PathVariable Long id) {

        supplierService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa nhà cung cấp thành công!", null));
    }

    @GetMapping("/import_template")
    public ResponseEntity<Resource> downloadImportTemplate() {

        InputStreamResource resource = new InputStreamResource(
                getClass().getResourceAsStream("/templates/supplier_import_template.xlsx")
        );

        if (!resource.exists()) { return ResponseEntity.notFound().build();}

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=supplier_import_template.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(resource);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importSuppliers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File upload rỗng");
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return ResponseEntity.badRequest().body("Chỉ hỗ trợ file .xlsx");
        }

        try {
            InputStream inputStream = file.getInputStream();
                System.out.println("Upload thành công");
//             TODO: xử lý đọc Excel (Apache POI)
//             importService.importSupplierFromExcel(inputStream);

            return ResponseEntity.ok("Upload và import thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi xử lý file: " + e.getMessage());
        }
    }
}

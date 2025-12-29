package com.example.project.service;

import java.io.ByteArrayInputStream;

public interface ExportService {

    ByteArrayInputStream exportLibrarianListToExcel();

    ByteArrayInputStream exportReaderListToExcel();

}

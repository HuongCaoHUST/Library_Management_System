package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;

public interface EmailService {

    void sendLibrarianAccountApproved(Librarian librarian, String rawPassword);
    void sendReaderAccountApproved(Reader reader, String rawPassword);

}

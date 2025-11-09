package com.example.project.util;

import com.example.project.model.Librarian;

public class SessionManager {
    private static Librarian currentLibrarian;

    public static void setCurrentLibrarian(Librarian librarian) {
        currentLibrarian = librarian;
    }

    public static Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }

    public static void clear() {
        currentLibrarian = null;
    }
}

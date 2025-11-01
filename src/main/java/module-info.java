module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires javafx.graphics;
    requires javafx.base;
    requires com.example.project;

    opens com.example.project to javafx.fxml;
    exports com.example.project;
    exports com.example.project.models;
    opens com.example.project.models to javafx.fxml;
    exports com.example.project.controller;
    opens com.example.project.controller to javafx.fxml;
}
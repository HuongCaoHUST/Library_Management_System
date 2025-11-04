package com.example.project.controller;
import com.example.project.model_controller.DocController;
import com.example.project.model_controller.UserController;
import com.example.project.models.Doc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
public class DocManagerController extends HomeControllerForAdmin {
    @FXML
    private TableView<Doc> tableView;
    @FXML
    private TableColumn<Doc, String> colID;
    @FXML
    private TableColumn<Doc, String> colTitle;
    @FXML
    private TableColumn<Doc, String> colAuthor;
    @FXML
    private TableColumn<Doc, String> colPubYear;
    @FXML
    private TableColumn<Doc, String> colCategory;
    @FXML
    private TableColumn<Doc, String> colType;
    @FXML
    private TableColumn<Doc, String> colStatus;
    @FXML
    private TableColumn<Doc, String> colDetail;
    @FXML
    private TextField searchField;
    @FXML
    private AnchorPane mainContent;
    private DocController docController;

    @FXML
    public void initialize() {
        super.initialize();
        docController = new DocController();
    }

    public void loadDocList() {
        ObservableList<Doc> docList = FXCollections.observableArrayList();
        Path path = Paths.get("data/docs.txt");

        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    // File docs.txt đang lưu 10 trường (docId,title,author,publisher,pubYear,category,shelfLoc,docType,accessUrl,status)
                    if (parts.length >= 10) {
                        docList.add(new Doc(parts));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể đọc danh sách tài liệu:\n" + e.getMessage());
        }

        // Map thuộc tính -> cột
        colID.setCellValueFactory(new PropertyValueFactory<>("docId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colPubYear.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colType.setCellValueFactory(new PropertyValueFactory<>("docType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Style thống nhất cho các cột dữ liệu
        String columnStyle = "-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Regular'; -fx-font-size: 16px;";
        colID.setStyle(columnStyle);
        colTitle.setStyle(columnStyle);
        colAuthor.setStyle(columnStyle);
        colPubYear.setStyle(columnStyle);
        colCategory.setStyle(columnStyle);
        colType.setStyle(columnStyle);
        colStatus.setStyle(columnStyle);

        // Cột nút "Chi tiết"
        colDetail.setCellFactory(col -> new TableCell<Doc, String>() {
            private final Button btn = new Button();
            {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("/com/example/project/logo/view.png"))
                );
                icon.setFitWidth(24);
                icon.setFitHeight(24);
                btn.setGraphic(icon);
                btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Doc d = getTableView().getItems().get(getIndex());
                    showDocDetails(d);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        tableView.setItems(docList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(tv -> {
            TableRow<Doc> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });
    }

    private void showDocDetails(Doc doc) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project/detail_doc_mini_form.fxml")
            );
            AnchorPane root = loader.load();

            // Nếu đã có DocController, lấy lại đối tượng đầy đủ theo ID; nếu không có thì dùng ngay doc truyền vào
            DetailDocController controller = loader.getController();
            controller.setDoc(doc);
            controller.setDocController(docController);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết tài liệu");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở chi tiết tài liệu:\n" + e.getMessage());
        }
    }
}

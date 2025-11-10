package com.example.project.controller;

import com.example.project.model.Librarian;
import com.example.project.util.SessionManager;
import com.example.project.util.SpringFxmlLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MenuBarController {

    @FXML private ImageView avatarImage;
    @FXML private MenuItem menuItemApproveAccount;
    @FXML private MenuItem menuItemReaderList;
    @FXML private MenuItem menuItemAddDocument;
    @FXML private AnchorPane rootPane;

    private ContextMenu dropdownMenu;

    @Autowired
    private SpringFxmlLoader fxmlLoader;

    @FXML
    private void initialize() {
        if (avatarImage == null) {
            return;
        }
        createDropdownMenu();
        setupHoverEffect();
    }

    @FXML
    private void openApprovalForm(ActionEvent event) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/reader_approval_list_form.fxml");
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((MenuItem) event.getSource())
                    .getParentPopup().getOwnerWindow();

            stage.setScene(scene);
            stage.setTitle("Duyệt tài khoản - Hệ thống quản lý thư viện");
            stage.centerOnScreen();
            stage.getIcons().add(new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")
            ));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReaderList(ActionEvent event) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/reader_list_form.fxml");
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((MenuItem) event.getSource())
                    .getParentPopup().getOwnerWindow();

            stage.setScene(scene);
            stage.setTitle("Danh sách tài khoản - Hệ thống quản lý thư viện");
            stage.centerOnScreen();
            stage.getIcons().add(new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")
            ));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDocumentListForm(ActionEvent event) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/document_list_form.fxml");
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((MenuItem) event.getSource())
                    .getParentPopup().getOwnerWindow();

            stage.setScene(scene);
            stage.setTitle("Danh sách tài liệu - Hệ thống quản lý thư viện");
            stage.centerOnScreen();
            stage.getIcons().add(new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")
            ));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddDocumentForm(ActionEvent event) {
        try {
            Parent root = fxmlLoader.load("/com/example/project/document_add_form.fxml");
            Stage stage = new Stage();
            stage.setTitle("Thêm tài liệu mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDropdownMenu() {
        dropdownMenu = new ContextMenu();

        MenuItem profileItem = new MenuItem("Thông tin cá nhân");
        profileItem.setOnAction(e -> onViewProfile());

        MenuItem logoutItem = new MenuItem("Đăng xuất");
        logoutItem.setOnAction(e -> onLogout());

        dropdownMenu.getItems().addAll(profileItem, new SeparatorMenuItem(), logoutItem);
    }

    private void setupHoverEffect() {
        avatarImage.setOnMouseEntered(event -> {
            double x = avatarImage.localToScreen(avatarImage.getBoundsInLocal()).getMinX();
            double y = avatarImage.localToScreen(avatarImage.getBoundsInLocal()).getMaxY();
            dropdownMenu.show(avatarImage, x, y);
        });

        avatarImage.setOnMouseExited(event -> {
            dropdownMenu.setOnHidden(e -> {});
        });
    }

    private void onViewProfile() {
        try {

            Parent root = fxmlLoader.load("/com/example/project/librarian_detail_form.fxml");
            Stage stage = new Stage();

            Librarian librarian = SessionManager.getCurrentLibrarian();
            LibrarianDetailController controller = (LibrarianDetailController) root.getUserData();
            controller.setLibrarian(librarian);

            stage.setTitle("Thông tin tài khoản");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLogout() {
        System.out.println("Đăng xuất...");
        try {
            Parent loginRoot = fxmlLoader.load("/com/example/project/login.fxml");
            Scene loginScene = new Scene(loginRoot);

            Stage stage = (Stage) avatarImage.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Đăng nhập - Hệ thống quản lý thư viện");
            stage.centerOnScreen();
            stage.getIcons().add(new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")
            ));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
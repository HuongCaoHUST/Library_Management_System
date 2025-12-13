package com.example.project.javafxcontroller;

import com.example.project.apiservice.LibrarianApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.RegisterRequest;
import com.example.project.model.Librarian;
import com.example.project.security.Permission;
import com.example.project.security.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuBarController {

    @FXML private ImageView avatarImage;
    @FXML private MenuItem menuItemLibrarianList;
    @FXML private MenuItem menuItemReaderList;
    private ContextMenu dropdownMenu;

    @FXML
    private void initialize() {

        UserSession session = UserSession.getInstance();
        menuItemLibrarianList.setVisible(session.hasPermission(Permission.LIBRARIAN_VIEW));
        menuItemReaderList.setVisible(session.hasPermission(Permission.READER_VIEW));

        if (avatarImage == null) {
            return;
        }
        createDropdownMenu();
        setupHoverEffect();
    }

    @FXML
    private void openLibrarianList(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/project/librarian_list_form.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((MenuItem) event.getSource())
                    .getParentPopup().getOwnerWindow();

            stage.setScene(scene);
            stage.setTitle("Danh sách chuyên viên - Hệ thống quản lý thư viện");
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/project/reader_list_form.fxml"));
            Parent root = fxmlLoader.load();

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
//        try {
//            Parent root = fxmlLoader.load("/com/example/project/document_list_form.fxml");
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) ((MenuItem) event.getSource())
//                    .getParentPopup().getOwnerWindow();
//
//            stage.setScene(scene);
//            stage.setTitle("Danh sách tài liệu - Hệ thống quản lý thư viện");
//            stage.centerOnScreen();
//            stage.getIcons().add(new javafx.scene.image.Image(
//                    getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")
//            ));
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void openGrnAddForm(ActionEvent event) {
//        try {
//            Parent root = fxmlLoader.load("/com/example/project/grn_add_form.fxml");
//            Stage stage = new Stage();
//            stage.setTitle("Thêm tài liệu mới");
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setScene(new Scene(root));
//            stage.showAndWait();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void createDropdownMenu() {
        dropdownMenu = new ContextMenu();

        MenuItem profileItem = new MenuItem("Thông tin cá nhân");
        profileItem.setOnAction(e -> onViewProfile());

        MenuItem changePasswordItem = new MenuItem("Đổi mật khẩu");
        changePasswordItem.setOnAction(e -> onChangePassword());

        MenuItem logoutItem = new MenuItem("Đăng xuất");
        logoutItem.setOnAction(e -> onLogout());

        dropdownMenu.getItems().addAll(profileItem, changePasswordItem, logoutItem);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/librarian_detail_form.fxml"));
            Parent root = loader.load();
            LibrarianDetailController controller = loader.getController();
            LibrarianApiService api = new LibrarianApiService();
            ApiResponse<Librarian> response = api.getMyLibrarianInfo();
            if (response.isSuccess() && response.getData() != null) {
                controller.setLibrarian(response.getData());
            } else {
                System.out.println("Không lấy được thông tin librarian: " + response.getMessage());
            }

            Stage stage = new Stage();
            stage.setTitle("Thông tin tài khoản");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onLogout() {
        System.out.println("LOGOUT");
        try {
            clearUserSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/login.fxml"));
            Parent loginRoot = loader.load();

            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) avatarImage.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Đăng nhập - Hệ thống quản lý thư viện");
            stage.centerOnScreen();
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/com/example/project/images/logo_HUB.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/change_password_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Đổi mật khẩu");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clearUserSession() {
        UserSession session = UserSession.getInstance();
        session.setToken(null);
        session.setRole(null);
        session.setPermissions(null);
    }

}
package com.example.project.javafxcontroller;

import com.example.project.apiservice.ReaderApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.model.Reader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ReaderMenuBarController {

    @FXML private ImageView avatarImage;
    @FXML private MenuItem menuItemApproveAccount;
    @FXML private MenuItem menuItemReaderList;
    @FXML private MenuItem menuItemAddDocument;
    @FXML private AnchorPane rootPane;

    private ContextMenu dropdownMenu;
    private final FXMLLoader fxmlLoader = new FXMLLoader();


    @FXML
    private void initialize() {
        if (avatarImage == null) {
            return;
        }
        createDropdownMenu();
        setupHoverEffect();
    }

    @FXML
    private void openDocumentListForm(ActionEvent event) {
        try {
            Parent root = fxmlLoader.load(getClass().getResource("/com/example/project/reader_document_list_form.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/reader_detail_form.fxml"));
            Parent root = loader.load();

            ReaderDetailController controller = loader.getController();
            ReaderApiService api = new ReaderApiService();
            ApiResponse<Reader> response = api.getMyReaderInfo();
            if (response.isSuccess() && response.getData() != null) {
                controller.setReader(response.getData());
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
        System.out.println("Đăng xuất...");
        try {
            Parent root = fxmlLoader.load(getClass().getResource("/com/example/project/login.fxml"));
            Scene loginScene = new Scene(root);
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
}
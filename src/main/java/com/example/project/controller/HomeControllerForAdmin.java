package com.example.project.controller;
import com.example.project.model_controller.UserController;
import com.example.project.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeControllerForAdmin extends LoadForm {
    @FXML
    private Label notificationBadge;

    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colMSSV;
    @FXML private TableColumn<User, String> colDOB;
    @FXML private TableColumn<User, String> colCCCD;
    @FXML private TableColumn<User, String> colWorkplace;
    @FXML private TableColumn<User, String> colDetail;
    @FXML private TableColumn<User, String> colApprove;
    @FXML private TableColumn<User, String> colReject;
    @FXML private Button btnDocumentManager;
    @FXML private ImageView avatarImage;

    private UserController userController;

    @FXML
    public void initialize() {
        userController = new UserController();
        super.notificationBadge = this.notificationBadge;
        updateNotificationBadge();
        setupReaderButtonMenu();
    }

    private void setupReaderButtonMenu() {
        ContextMenu documentMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Nhận tài liệu");
        MenuItem item2 = new MenuItem("Mượn tài liệu");
        MenuItem item3 = new MenuItem("Trả tài liệu");
        MenuItem item4 = new MenuItem("Xử lý báo, tạp chí");
        MenuItem item5 = new MenuItem("Xác nhận nghĩa vụ thư viện");

        documentMenu.getItems().addAll(item1, item2, item3, item4, item5);

        item1.setOnAction(e -> handleDocumentReceive());
//        approvalItem.setOnAction(e -> openApproval());

        btnDocumentManager.setOnMouseEntered(event -> {
            if (!documentMenu.isShowing()) {
                Bounds bounds = btnDocumentManager.localToScreen(btnDocumentManager.getBoundsInLocal());
                double menuX = bounds.getMaxX();
                double menuY = bounds.getMaxY();
                documentMenu.show(btnDocumentManager, menuX - 160, menuY);
            }
        });

        btnDocumentManager.setOnMouseExited(event -> {
            btnDocumentManager.setOnMouseExited(ev -> {
                btnDocumentManager.setOnMouseExited(null);
                documentMenu.hide();
            });
        });
    }

    @FXML
    protected void handleApproveAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/account_approval_form.fxml"));
            Parent root = loader.load();
            ApprovalAccountController controller = loader.getController();
            controller.loadRegisterQueue();
            controller.updateNotificationBadge();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Phê duyệt tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void handleAccountManager(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/account_manager_form.fxml"));
            Parent root = loader.load();
            AccountManagerController controller = loader.getController();
            controller.loadAccountList();
            controller.updateNotificationBadge();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quản lý tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleAccountInfor(ActionEvent event) {
        loadForm(event, "/com/example/project/detail_account_infor_form.fxml", "Thông tin tài khoản");
    }

    protected void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")));
        alert.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }

    @FXML
    protected void handleDocumentReceive() {
        try {
            FXMLLoader loader = loadFormInStage("/com/example/project/document_receive_form.fxml", "Nhận tài liệu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected FXMLLoader loadFormInStage(String fxmlPath, String title) throws IOException {
        Stage currentStage = (Stage) avatarImage.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        currentStage.setTitle(title);
        currentStage.setScene(new Scene(root));

        return loader; // trả về để lấy controller
    }
}

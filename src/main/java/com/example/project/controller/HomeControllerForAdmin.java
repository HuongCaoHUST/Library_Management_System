package com.example.project.controller;
import com.example.project.model_controller.UserController;
import com.example.project.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.Window;
import javafx.stage.Modality;

public class HomeControllerForAdmin extends LoadForm {
    @FXML
    private Label notificationBadge;

//    @FXML private TableView<User> tableView;
//    @FXML private TableColumn<User, String> colName;
//    @FXML private TableColumn<User, String> colMSSV;
//    @FXML private TableColumn<User, String> colDOB;
//    @FXML private TableColumn<User, String> colCCCD;
//    @FXML private TableColumn<User, String> colWorkplace;
//    @FXML private TableColumn<User, String> colDetail;
//    @FXML private TableColumn<User, String> colApprove;
//    @FXML private TableColumn<User, String> colReject;

    @FXML
    public void initialize() {
        super.notificationBadge = this.notificationBadge;
        updateNotificationBadge();
    }
    @FXML
    protected void handleApproveAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/account_approval_form.fxml"));
            Parent root = loader.load();
            ApprovalAccountController controller = loader.getController();
            controller.loadRegisterQueue();
            controller.updateNotificationBadge();

            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.setTitle("Phê duyệt tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình Phê duyệt tài khoản:\n" + e.getMessage());
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

            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.setTitle("Danh sách tài khoản");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình Danh sách tài khoản:\n" + e.getMessage());
        }
    }

    @FXML
    protected void handleDocManager(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project/doc_manager_form.fxml")
            );
            Parent root = loader.load();

            // Đổi DocManagerController thành tên controller thật trong FXML nếu khác
            DocManagerController controller = loader.getController();
            controller.loadDocList();
            controller.updateNotificationBadge();

            Stage stage = getStage(event);
            stage.setScene(new Scene(root));
            stage.setTitle("Danh sách tài liệu");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình Danh sách tài liệu:\n" + e.getMessage());
        }
    }

    public void handleDocAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project/doc_add_form.fxml")
            );
            Parent root = loader.load();

            // Lấy stage hiện tại làm owner
            Stage owner = getStage(event);

            // Tạo cửa sổ modal đè lên
            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL); // chặn cửa sổ phía dưới
            dialog.setTitle("Thêm tài liệu");
            dialog.setScene(new Scene(root));
            dialog.getIcons().add(new Image(
                    getClass().getResourceAsStream("/com/example/project/logo/logo_HUB.png")
            ));
            dialog.setResizable(false);
            dialog.centerOnScreen();

            // Mở và chờ đến khi form được đóng
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình Thêm tài liệu:\n" + e.getMessage());
        }
    }
    
    public void handleApproveReq(ActionEvent actionEvent) {
    }

    // Tiện ích: lấy Stage từ ActionEvent cho cả Button (Node) và MenuItem
    private Stage getStage(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof Node node) {
            return (Stage) node.getScene().getWindow();
        } else if (src instanceof MenuItem item) {
            Window owner = item.getParentPopup() != null ? item.getParentPopup().getOwnerWindow() : null;
            if (owner instanceof Stage stage) {
                return stage;
            }
            throw new IllegalStateException("Không lấy được Stage từ MenuItem (ownerWindow null)");
        } else {
            throw new IllegalStateException("Unsupported event source: " + src);
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
}

package com.example.project.javafxcontroller;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.model.Role;
import com.example.project.service.RoleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleListController {

    @FXML private TableView<RoleRequest> roleTable;
    @FXML private TableColumn<RoleRequest, String> roleNameCol;

    @FXML private TableView<PermissionRequest> permissionTable;
    @FXML private TableColumn<PermissionRequest, String> permissionNameCol;
    @FXML private TableColumn<PermissionRequest, String> permissionDescriptionCol;

    private final RoleService roleService = new RoleService();

    @FXML
    public void initialize() {
        roleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleNameCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getRoleName())
        );

        permissionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionNameCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPermissionName())
        );

        permissionDescriptionCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getDescription() == null
                        ? "chưa có mô tả"
                        : data.getValue().getDescription()
                )
        );

        roleTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldRole, newRole) -> {
                    if (newRole != null) {
                        loadPermissions(newRole);
                    } else {
                        permissionTable.getItems().clear();
                    }
                });

        loadRoles();
    }

    private void loadRoles() {
        try {
            ObservableList<RoleRequest> roles =
                    FXCollections.observableArrayList(roleService.getRoles());

            roleTable.setItems(roles);

            if (!roles.isEmpty()) {
                roleTable.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadPermissions (RoleRequest role) {
        if (role.getPermissions() == null) {
            permissionTable.getItems().clear();
            role.getPermissions().forEach(p ->
                    System.out.println("Permission: " + p.getPermissionName() + " | Desc: " + p.getDescription())
            );
            return;
        }
        ObservableList<PermissionRequest> permissions = FXCollections.observableArrayList(role.getPermissions());
        permissionTable.setItems(permissions);
    }

    @FXML
    protected void openRoleAddForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/role_add_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm vai trò");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openPermissionAddForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/permission_add_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm quyền");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openRoleUpdateForm(ActionEvent event) {
        try {
            RoleRequest selectedRole = roleTable.getSelectionModel().getSelectedItem();
            if (selectedRole == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một vai trò để cập nhật!");
                alert.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/role_update_form.fxml"));
            Parent root = loader.load();

            RoleUpdateController controller = loader.getController();
            controller.setRole(selectedRole);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật vai trò");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


package com.example.project.javafxcontroller;

import com.example.project.apiservice.RoleApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.*;
import com.example.project.service.PermissionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoleUpdateController {

    @FXML private TextField txtRoleName;
    @FXML private TextField txtRoleDescription;
    @FXML private TableView<PermissionRequest> assignedPermissionTable;
    @FXML private TableColumn<PermissionRequest, String> assignedPermissionCol;

    @FXML private TableView<PermissionRequest> availablePermissionTable;
    @FXML private TableColumn<PermissionRequest, String> availablePermissionCol;
    @FXML private Button addPermissionBtn;
    @FXML private Button revokePermissionBtn;

    private PermissionService permissionService;
    private Long thisRoleId;

    @FXML
    private void initialize() {
        permissionService = new PermissionService();
        assignedPermissionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availablePermissionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setRole(RoleRequest role) {
        thisRoleId = role.getRoleId();
        txtRoleName.setText(role.getRoleName());
        txtRoleDescription.setText(role.getDescription());

        // Assigned Permission
        assignedPermissionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        assignedPermissionCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPermissionName())
        );
        ObservableList<PermissionRequest> assignedPermission = FXCollections.observableArrayList(role.getPermissions());
        FXCollections.sort(assignedPermission, (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );
        assignedPermissionTable.setItems(assignedPermission);

        // Available Permission
        List<PermissionRequest> allPermission = permissionService.getPermissions();
        ObservableList<PermissionRequest> availablePermission = FXCollections.observableArrayList(allPermission);

        availablePermission.removeAll(assignedPermission);
        availablePermissionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availablePermissionCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPermissionName())
        );
        FXCollections.sort(availablePermission, (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );
        availablePermissionTable.setItems(availablePermission);
    }

    @FXML
    private void handleAddPermission() {
        ObservableList<PermissionRequest> selected = availablePermissionTable.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            return;
        }

        assignedPermissionTable.getItems().addAll(selected);
        availablePermissionTable.getItems().removeAll(selected);
        FXCollections.sort(assignedPermissionTable.getItems(), (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );
        FXCollections.sort(availablePermissionTable.getItems(), (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );

        availablePermissionTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRevokePermission() {
        ObservableList<PermissionRequest> selected = assignedPermissionTable.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            return;
        }

        availablePermissionTable.getItems().addAll(selected);
        assignedPermissionTable.getItems().removeAll(selected);
        FXCollections.sort(assignedPermissionTable.getItems(), (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );

        FXCollections.sort(availablePermissionTable.getItems(), (p1, p2) ->
                p1.getPermissionName().compareToIgnoreCase(p2.getPermissionName())
        );
        assignedPermissionTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onUpdateRole() {
        List<Long> permissionIds = assignedPermissionTable.getItems().stream()
                .map(PermissionRequest::getPermissionId)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        RoleApiService api = new RoleApiService();
        try {
            ApiResponse<RoleRequest> response = api.updateRole(thisRoleId, permissionIds);

            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", response.getMessage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Không thành công", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối tới server"
            );
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

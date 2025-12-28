package com.example.project.javafxcontroller;

import com.example.project.apiservice.PermissionApiService;
import com.example.project.apiservice.RoleApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.*;
import com.example.project.model.Permission;
import com.example.project.service.PermissionService;
import com.example.project.service.RoleService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionAddController {

    @FXML private TextField txtPermissionName;
    @FXML private TextField txtPermissionDescription;

    @FXML private TableView<RoleRequest> roleTable;
    @FXML private TableColumn<RoleRequest, Boolean> selectCol;
    @FXML private TableColumn<RoleRequest, String> roleNameCol;
    @FXML private TableColumn<RoleRequest, String> roleDescriptionCol;
    private final RoleService roleService = new RoleService();
    private final PermissionService permissionService = new PermissionService();
    private final Map<RoleRequest, BooleanProperty> selectedMap = new HashMap<>();

    @FXML
    public void initialize() {
        roleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleNameCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getRoleName())
        );
        roleDescriptionCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getDescription() == null
                        ? "chưa có mô tả"
                        : data.getValue().getDescription()
                )
        );

        // Setup select colum
        selectCol.setCellValueFactory(param -> {
            RoleRequest role = param.getValue();
            selectedMap.putIfAbsent(role, new SimpleBooleanProperty(false));

            return selectedMap.get(role);
        });

        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        roleTable.setEditable(true);
        loadRoles();
    }

    @FXML
    private void onAddPermission() {
        if (!validateForm()) {
            return;
        }

        PermissionAddToRoleRequest dto = buildRoleDto();
        System.out.println("DTO: " + dto);
        PermissionApiService api = new PermissionApiService();
        try {
            ApiResponse<PermissionRequest> response = api.addPermissionToRole(dto);

            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", response.getMessage());
                clearForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Không thành công", response.getMessage());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối tới server"
            );
        }
    }

    private PermissionAddToRoleRequest buildRoleDto() {

        PermissionAddToRoleRequest dto = new PermissionAddToRoleRequest();
        dto.setName(txtPermissionName.getText().trim());
        dto.setDescription(txtPermissionDescription.getText().trim());

        List<Long> selectedRoles = selectedMap.entrySet()
                .stream()
                .filter(e -> e.getValue().get())
                .map(e -> e.getKey().getRoleId())
                .toList();

        dto.setRoleIds(selectedRoles);
        return dto;
    }

    private boolean validateForm() {

        if (txtPermissionName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên quyền!");
            return false;
        }

        if (txtPermissionDescription.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập mô tả của quyền!");
            return false;
        }

        return true;
    }

    private void loadRoles() {
        try {
            ObservableList<RoleRequest> roles = FXCollections.observableArrayList(roleService.getRoles());

            roleTable.setItems(roles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearForm() {
        txtPermissionName.clear();
        txtPermissionDescription.clear();
    }
}

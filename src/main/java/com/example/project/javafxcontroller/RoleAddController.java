package com.example.project.javafxcontroller;

import com.example.project.apiservice.RoleApiService;
import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.PermissionRequest2;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.request.RoleRequest2;
import com.example.project.service.PermissionService;
import com.example.project.service.RoleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleAddController{

    @FXML private TextField txtRoleName;
    @FXML private TextField txtRoleDescription;
    @FXML private ComboBox<String> cbRoleList;

    @FXML private TableView<PermissionRequest> permissionTable;
    @FXML private TableColumn<PermissionRequest, Boolean> selectCol;
    @FXML private TableColumn<PermissionRequest, String> permissionNameCol;
    @FXML private TableColumn<PermissionRequest, String> permissionDescriptionCol;
    private final RoleService roleService = new RoleService();
    private final PermissionService permissionService = new PermissionService();
    private Map<PermissionRequest, Boolean> selectedMap = new HashMap<>();

    @FXML
    public void initialize() {
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

        // Setup select colum
        selectCol.setCellFactory(tc -> new CheckBoxTableCell<PermissionRequest, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    PermissionRequest permission = getTableView().getItems().get(getIndex());
                    checkBox.setOnAction(e -> selectedMap.put(permission, checkBox.isSelected()));
                    if (selectedMap.containsKey(permission)) {
                        checkBox.setSelected(selectedMap.get(permission));
                    }
                    setGraphic(checkBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });

        permissionTable.setEditable(true);

        loadRolesToComboBox();
        loadPermissions();

        cbRoleList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Không")) {
                handleRoleSelected(newVal, selectedMap);
            } else {
                selectedMap.clear();
                permissionTable.refresh();
            }
        });
    }

    @FXML
    private void onAddRole() {
        if (!validateForm()) {
            return;
        }

        RoleRequest2 dto = buildRoleDto();

        RoleApiService api = new RoleApiService();
        try {
            ApiResponse<RoleRequest> response = api.addRole(dto);

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

    private RoleRequest2 buildRoleDto() {

        RoleRequest2 dto = new RoleRequest2();
        dto.setName(txtRoleName.getText().trim());
        dto.setDescription(txtRoleDescription.getText().trim());

        List<PermissionRequest2> selectedPermissions = selectedMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .map(p -> {
                    PermissionRequest2 pr = new PermissionRequest2();
                    pr.setName(p.getPermissionName());
                    return pr;
                })
                .toList();

        dto.setPermissions(selectedPermissions);
        return dto;
    }

    private boolean validateForm() {

        if (txtRoleName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên vai trò!");
            return false;
        }

        if (txtRoleDescription.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập mô tả của vai trò!");
            return false;
        }

        return true;
    }

    private void loadRolesToComboBox() {
        try {
            ObservableList<String> roleNames = FXCollections.observableArrayList();
            roleNames.add("Không");
            roleService.getRoles().forEach(role -> {
                roleNames.add(role.getRoleName());
            });

            cbRoleList.setItems(roleNames);

            if (!roleNames.isEmpty()) {
                cbRoleList.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPermissions() {
        try {
            ObservableList<PermissionRequest> permissions =
                    FXCollections.observableArrayList(permissionService.getPermissions());

            permissionTable.setItems(permissions);

            if (!permissions.isEmpty()) {
                permissionTable.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRoleSelected(String roleName, Map<PermissionRequest, Boolean> selectedMap) {
        try {
            List<PermissionRequest> rolePermissions = roleService.getPermissionsByRoleName(roleName);

            for (PermissionRequest permission : permissionTable.getItems()) {
                boolean isAssigned = rolePermissions.stream()
                        .anyMatch(p -> p.getPermissionName().equals(permission.getPermissionName()));
                selectedMap.put(permission, isAssigned);
            }

            permissionTable.refresh();

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
        txtRoleName.clear();
        txtRoleDescription.clear();
    }
}

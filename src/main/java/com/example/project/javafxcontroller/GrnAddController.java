package com.example.project.javafxcontroller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class GrnAddController implements Initializable {

    // ========== FXML Components - Thông tin chung ==========
    @FXML private TextField txtMaHoaDon;
    @FXML private TextField txtDonViCungCap;
    @FXML private TextField txtBenNhan;
    @FXML private TextField txtBenGiao;
    @FXML private DatePicker dpNgayNhan;

    // ========== FXML Components - Bảng tài liệu ==========
    @FXML private TableView<TaiLieuNhap> tableTaiLieu;
    @FXML private TableColumn<TaiLieuNhap, Integer> colSTT;
    @FXML private TableColumn<TaiLieuNhap, String> colTheLoai;
    @FXML private TableColumn<TaiLieuNhap, String> colNhanDe;
    @FXML private TableColumn<TaiLieuNhap, String> colTacGia;
    @FXML private TableColumn<TaiLieuNhap, Integer> colNamXuatBan;
    @FXML private TableColumn<TaiLieuNhap, String> colViTriKe;
    @FXML private TableColumn<TaiLieuNhap, Integer> colSoLuong;
    @FXML private TableColumn<TaiLieuNhap, Long> colDonGia;
    @FXML private TableColumn<TaiLieuNhap, String> colSoDKCB;
    @FXML private TableColumn<TaiLieuNhap, Void> colXoa;

    // ========== FXML Components - Buttons ==========
    @FXML private Button btnThemHang;
    @FXML private Button btnLuu;
    @FXML private Button btnXoaTrang;
    @FXML private Button btnHuy;

    // ========== Data ==========
    private final ObservableList<TaiLieuNhap> danhSachTaiLieu = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatePicker();
        setupTable();
        setupTableEditing();
    }

    /**
     * Cấu hình DatePicker
     */
    private void setupDatePicker() {
        dpNgayNhan.setValue(LocalDate.now());
    }

    /**
     * Cấu hình bảng tài liệu
     */
    private void setupTable() {
        // Cột STT - tự động đánh số
        colSTT.setCellValueFactory(cellData -> {
            int index = tableTaiLieu.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });

        // Cột Thể loại
        colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));

        // Cột Nhan đề
        colNhanDe.setCellValueFactory(new PropertyValueFactory<>("nhanDe"));

        // Cột Tác giả
        colTacGia.setCellValueFactory(new PropertyValueFactory<>("tacGia"));

        // Cột Năm xuất bản
        colNamXuatBan.setCellValueFactory(new PropertyValueFactory<>("namXuatBan"));

        // Cột Vị trí kệ
        colViTriKe.setCellValueFactory(new PropertyValueFactory<>("viTriKe"));

        // Cột Số lượng
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        // Cột Đơn giá - format tiền tệ
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colDonGia.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    setText(formatter.format(item) + " đ");
                }
            }
        });

        // Cột Số ĐKCB
        colSoDKCB.setCellValueFactory(new PropertyValueFactory<>("soDKCB"));

        // Cột Xoá - nút xoá
        setupDeleteColumn();

        // Gán dữ liệu cho bảng
        tableTaiLieu.setItems(danhSachTaiLieu);
        tableTaiLieu.setEditable(true);
    }

    /**
     * Cấu hình cột xoá với nút bấm
     */
    private void setupDeleteColumn() {
        colXoa.setCellFactory(column -> new TableCell<>() {
            private final Button btnXoa = new Button("✕");

            {
                btnXoa.getStyleClass().add("grn-btn-delete-row");

                btnXoa.setOnAction(event -> {
                    TaiLieuNhap taiLieu = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Xác nhận xoá");
                    confirm.setHeaderText("Xoá tài liệu");
                    confirm.setContentText("Bạn có chắc muốn xoá tài liệu \"" +
                            (taiLieu.getNhanDe() != null ? taiLieu.getNhanDe() : "chưa có tên") + "\"?");

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        danhSachTaiLieu.remove(taiLieu);
                        tableTaiLieu.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnXoa);
            }
        });
    }

    /**
     * Cấu hình cho phép chỉnh sửa trực tiếp trên bảng
     */
    private void setupTableEditing() {
        // Cho phép chỉnh sửa cột Thể loại
        colTheLoai.setCellFactory(TextFieldTableCell.forTableColumn());
        colTheLoai.setOnEditCommit(event -> {
            event.getRowValue().setTheLoai(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Nhan đề
        colNhanDe.setCellFactory(TextFieldTableCell.forTableColumn());
        colNhanDe.setOnEditCommit(event -> {
            event.getRowValue().setNhanDe(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Tác giả
        colTacGia.setCellFactory(TextFieldTableCell.forTableColumn());
        colTacGia.setOnEditCommit(event -> {
            event.getRowValue().setTacGia(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Năm xuất bản
        colNamXuatBan.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colNamXuatBan.setOnEditCommit(event -> {
            event.getRowValue().setNamXuatBan(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Vị trí kệ
        colViTriKe.setCellFactory(TextFieldTableCell.forTableColumn());
        colViTriKe.setOnEditCommit(event -> {
            event.getRowValue().setViTriKe(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Số lượng
        colSoLuong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoLuong.setOnEditCommit(event -> {
            event.getRowValue().setSoLuong(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Đơn giá
        colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        colDonGia.setOnEditCommit(event -> {
            event.getRowValue().setDonGia(event.getNewValue());
        });

        // Cho phép chỉnh sửa cột Số ĐKCB
        colSoDKCB.setCellFactory(TextFieldTableCell.forTableColumn());
        colSoDKCB.setOnEditCommit(event -> {
            event.getRowValue().setSoDKCB(event.getNewValue());
        });
    }

    // ==================== EVENT HANDLERS ====================

    /**
     * Xử lý sự kiện thêm hàng mới
     */
    @FXML
    private void handleThemHang() {
        TaiLieuNhap taiLieuMoi = new TaiLieuNhap();
        danhSachTaiLieu.add(taiLieuMoi);

        // Cuộn xuống hàng mới thêm
        tableTaiLieu.scrollTo(taiLieuMoi);

        // Focus vào hàng mới để người dùng có thể nhập ngay
        int newIndex = danhSachTaiLieu.size() - 1;
        tableTaiLieu.getSelectionModel().select(newIndex);
        tableTaiLieu.getFocusModel().focus(newIndex, colTheLoai);
    }

    /**
     * Xử lý sự kiện lưu phiếu
     */
    @FXML
    private void handleLuuPhieu() {
        // Kiểm tra dữ liệu đầu vào
        if (!validateInput()) {
            return;
        }

        // Hiển thị thông tin phiếu (để debug)
        System.out.println("========== PHIẾU NHẬP KHO ==========");
        System.out.println("Mã hoá đơn: " + txtMaHoaDon.getText());
        System.out.println("Đơn vị cung cấp: " + txtDonViCungCap.getText());
        System.out.println("Bên nhận: " + txtBenNhan.getText());
        System.out.println("Bên giao: " + txtBenGiao.getText());
        System.out.println("Ngày nhận: " + dpNgayNhan.getValue());
        System.out.println("Số lượng tài liệu: " + danhSachTaiLieu.size());
        System.out.println("=====================================");

        for (TaiLieuNhap tl : danhSachTaiLieu) {
            System.out.println("- " + tl.getNhanDe() + " | " + tl.getTacGia() +
                    " | SL: " + tl.getSoLuong() + " | Vị trí: " + tl.getViTriKe());
        }

        // TODO: Lưu vào database
        // phieuNhapKhoService.save(...)

        // Thông báo thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Phiếu nhập kho đã được lưu thành công!\nSố lượng tài liệu: " + danhSachTaiLieu.size());
        alert.showAndWait();

        // Đóng cửa sổ sau khi lưu thành công
        clearAllData();
        Stage stage = (Stage) btnLuu.getScene().getWindow();
        stage.close();
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (txtMaHoaDon.getText() == null || txtMaHoaDon.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập mã hoá đơn\n");
        }

        if (txtDonViCungCap.getText() == null || txtDonViCungCap.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập đơn vị cung cấp\n");
        }

        if (txtBenNhan.getText() == null || txtBenNhan.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên bên nhận\n");
        }

        if (txtBenGiao.getText() == null || txtBenGiao.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên bên giao\n");
        }

        if (dpNgayNhan.getValue() == null) {
            errors.append("- Vui lòng chọn ngày nhận\n");
        }

        if (danhSachTaiLieu.isEmpty()) {
            errors.append("- Vui lòng thêm ít nhất một tài liệu\n");
        } else {
            // Kiểm tra từng tài liệu
            for (int i = 0; i < danhSachTaiLieu.size(); i++) {
                TaiLieuNhap tl = danhSachTaiLieu.get(i);
                if (tl.getNhanDe() == null || tl.getNhanDe().trim().isEmpty()) {
                    errors.append("- Tài liệu dòng " + (i + 1) + ": Thiếu nhan đề\n");
                }
                if (tl.getSoLuong() <= 0) {
                    errors.append("- Tài liệu dòng " + (i + 1) + ": Số lượng phải lớn hơn 0\n");
                }
                if (tl.getViTriKe() == null || tl.getViTriKe().trim().isEmpty()) {
                    errors.append("- Tài liệu dòng " + (i + 1) + ": Thiếu vị trí kệ\n");
                }
            }
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Vui lòng kiểm tra lại thông tin:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Xử lý sự kiện xoá trắng form
     */
    @FXML
    private void handleXoaTrang() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xoá trắng form");
        confirm.setContentText("Bạn có chắc muốn xoá toàn bộ dữ liệu đã nhập?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clearAllData();
        }
    }

    /**
     * Xử lý sự kiện huỷ
     */
    @FXML
    private void handleHuy() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Huỷ thao tác");
        confirm.setContentText("Bạn có chắc muốn huỷ? Mọi dữ liệu chưa lưu sẽ bị mất.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clearAllData();
            // Đóng cửa sổ hiện tại
            Stage stage = (Stage) btnHuy.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Xóa toàn bộ dữ liệu trong form
     */
    private void clearAllData() {
        // Xoá thông tin chung
        txtMaHoaDon.clear();
        txtDonViCungCap.clear();
        txtBenNhan.clear();
        txtBenGiao.clear();
        dpNgayNhan.setValue(LocalDate.now());

        // Xoá danh sách tài liệu
        danhSachTaiLieu.clear();
    }

    // ==================== GETTER METHODS (cho việc test/debug) ====================

    public ObservableList<TaiLieuNhap> getDanhSachTaiLieu() {
        return danhSachTaiLieu;
    }

    // ==================== INNER CLASS - Model TaiLieuNhap ====================

    /**
     * Model class cho mỗi tài liệu trong phiếu nhập kho
     */
    public static class TaiLieuNhap {
        private final StringProperty theLoai = new SimpleStringProperty("");
        private final StringProperty nhanDe = new SimpleStringProperty("");
        private final StringProperty tacGia = new SimpleStringProperty("");
        private final IntegerProperty namXuatBan = new SimpleIntegerProperty(LocalDate.now().getYear());
        private final StringProperty viTriKe = new SimpleStringProperty("");
        private final IntegerProperty soLuong = new SimpleIntegerProperty(1);
        private final LongProperty donGia = new SimpleLongProperty(0);
        private final StringProperty soDKCB = new SimpleStringProperty("");

        public TaiLieuNhap() {
        }

        public TaiLieuNhap(String theLoai, String nhanDe, String tacGia, int namXuatBan,
                           String viTriKe, int soLuong, long donGia, String soDKCB) {
            this.theLoai.set(theLoai);
            this.nhanDe.set(nhanDe);
            this.tacGia.set(tacGia);
            this.namXuatBan.set(namXuatBan);
            this.viTriKe.set(viTriKe);
            this.soLuong.set(soLuong);
            this.donGia.set(donGia);
            this.soDKCB.set(soDKCB);
        }

        // ===== Thể loại =====
        public String getTheLoai() {
            return theLoai.get();
        }

        public void setTheLoai(String value) {
            theLoai.set(value);
        }

        public StringProperty theLoaiProperty() {
            return theLoai;
        }

        // ===== Nhan đề =====
        public String getNhanDe() {
            return nhanDe.get();
        }

        public void setNhanDe(String value) {
            nhanDe.set(value);
        }

        public StringProperty nhanDeProperty() {
            return nhanDe;
        }

        // ===== Tác giả =====
        public String getTacGia() {
            return tacGia.get();
        }

        public void setTacGia(String value) {
            tacGia.set(value);
        }

        public StringProperty tacGiaProperty() {
            return tacGia;
        }

        // ===== Năm xuất bản =====
        public int getNamXuatBan() {
            return namXuatBan.get();
        }

        public void setNamXuatBan(int value) {
            namXuatBan.set(value);
        }

        public IntegerProperty namXuatBanProperty() {
            return namXuatBan;
        }

        // ===== Vị trí kệ =====
        public String getViTriKe() {
            return viTriKe.get();
        }

        public void setViTriKe(String value) {
            viTriKe.set(value);
        }

        public StringProperty viTriKeProperty() {
            return viTriKe;
        }

        // ===== Số lượng =====
        public int getSoLuong() {
            return soLuong.get();
        }

        public void setSoLuong(int value) {
            soLuong.set(value);
        }

        public IntegerProperty soLuongProperty() {
            return soLuong;
        }

        // ===== Đơn giá =====
        public long getDonGia() {
            return donGia.get();
        }

        public void setDonGia(long value) {
            donGia.set(value);
        }

        public LongProperty donGiaProperty() {
            return donGia;
        }

        // ===== Số ĐKCB =====
        public String getSoDKCB() {
            return soDKCB.get();
        }

        public void setSoDKCB(String value) {
            soDKCB.set(value);
        }

        public StringProperty soDKCBProperty() {
            return soDKCB;
        }

        @Override
        public String toString() {
            return "TaiLieuNhap{" +
                    "nhanDe='" + nhanDe.get() + '\'' +
                    ", tacGia='" + tacGia.get() + '\'' +
                    ", viTriKe='" + viTriKe.get() + '\'' +
                    ", soLuong=" + soLuong.get() +
                    '}';
        }
    }
}
package com.example.project.javafxcontroller;

import com.example.project.model.Grn;
import com.example.project.model.GrnDetail;
import com.example.project.service.GrnService;
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
import javafx.util.converter.DoubleStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class GrnAddController implements Initializable {

    // ========== FXML Components - Thông tin chung ==========
    @FXML private TextField txtReceiptId;
    @FXML private TextField txtSupplier;
    @FXML private TextField txtReceiver;
    @FXML private TextField txtDeliverer;
    @FXML private DatePicker dpReceiveDate;

    // ========== FXML Components - Bảng tài liệu ==========
    @FXML private TableView<TaiLieuNhapUI> tableTaiLieu;
    @FXML private TableColumn<TaiLieuNhapUI, Integer> colSTT;
    @FXML private TableColumn<TaiLieuNhapUI, String> colCategory;
    @FXML private TableColumn<TaiLieuNhapUI, String> colTitle;
    @FXML private TableColumn<TaiLieuNhapUI, String> colAuthor;

    // ✅ THÊM CỘT MỚI: Nhà xuất bản
    @FXML private TableColumn<TaiLieuNhapUI, String> colPublisher;

    @FXML private TableColumn<TaiLieuNhapUI, Integer> colPublicationYear;
    @FXML private TableColumn<TaiLieuNhapUI, String> colShelfLocation;
    @FXML private TableColumn<TaiLieuNhapUI, Integer> colAvailableCopies;
    @FXML private TableColumn<TaiLieuNhapUI, Double> colCoverPrice;
    @FXML private TableColumn<TaiLieuNhapUI, String> colDkcbCode;
    @FXML private TableColumn<TaiLieuNhapUI, Void> colXoa;

    // ========== FXML Components - Buttons ==========
    @FXML private Button btnThemHang;
    @FXML private Button btnLuu;
    @FXML private Button btnXoaTrang;
    @FXML private Button btnHuy;

    // ========== Services ==========
    @Autowired
    private GrnService grnService;

    // ========== Data ==========
    private final ObservableList<TaiLieuNhapUI> danhSachTaiLieu = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatePicker();
        setupTable();
        setupTableEditing();
    }

    private void setupDatePicker() {
        dpReceiveDate.setValue(LocalDate.now());
    }

    private void setupTable() {
        // Cột STT - tự động đánh số
        colSTT.setCellValueFactory(cellData -> {
            int index = tableTaiLieu.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });

        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        // ✅ THÊM CỘT PUBLISHER
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        colPublicationYear.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        colShelfLocation.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
        colAvailableCopies.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

        // Cột Đơn giá - format tiền tệ
        colCoverPrice.setCellValueFactory(new PropertyValueFactory<>("coverPrice"));
        colCoverPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                    setText(formatter.format(item) + " đ");
                }
            }
        });

        colDkcbCode.setCellValueFactory(new PropertyValueFactory<>("dkcbCode"));
        setupDeleteColumn();

        tableTaiLieu.setItems(danhSachTaiLieu);
        tableTaiLieu.setEditable(true);
    }

    private void setupDeleteColumn() {
        colXoa.setCellFactory(column -> new TableCell<>() {
            private final Button btnXoa = new Button("✕");

            {
                btnXoa.getStyleClass().add("grn-btn-delete-row");
                btnXoa.setOnAction(event -> {
                    TaiLieuNhapUI taiLieu = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Xác nhận xoá");
                    confirm.setHeaderText("Xoá tài liệu");
                    confirm.setContentText("Bạn có chắc muốn xoá tài liệu \"" +
                            (taiLieu.getTitle() != null ? taiLieu.getTitle() : "chưa có tên") + "\"?");

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

    private void setupTableEditing() {
        colCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        colCategory.setOnEditCommit(event -> event.getRowValue().setCategory(event.getNewValue()));

        colTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        colTitle.setOnEditCommit(event -> event.getRowValue().setTitle(event.getNewValue()));

        colAuthor.setCellFactory(TextFieldTableCell.forTableColumn());
        colAuthor.setOnEditCommit(event -> event.getRowValue().setAuthor(event.getNewValue()));

        // ✅ THÊM EDITING CHO PUBLISHER
        colPublisher.setCellFactory(TextFieldTableCell.forTableColumn());
        colPublisher.setOnEditCommit(event -> event.getRowValue().setPublisher(event.getNewValue()));

        colPublicationYear.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colPublicationYear.setOnEditCommit(event -> event.getRowValue().setPublicationYear(event.getNewValue()));

        colShelfLocation.setCellFactory(TextFieldTableCell.forTableColumn());
        colShelfLocation.setOnEditCommit(event -> event.getRowValue().setShelfLocation(event.getNewValue()));

        colAvailableCopies.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colAvailableCopies.setOnEditCommit(event -> event.getRowValue().setAvailableCopies(event.getNewValue()));

        colCoverPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCoverPrice.setOnEditCommit(event -> event.getRowValue().setCoverPrice(event.getNewValue()));

        colDkcbCode.setCellFactory(TextFieldTableCell.forTableColumn());
        colDkcbCode.setOnEditCommit(event -> event.getRowValue().setDkcbCode(event.getNewValue()));
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleThemHang() {
        TaiLieuNhapUI taiLieuMoi = new TaiLieuNhapUI();
        danhSachTaiLieu.add(taiLieuMoi);
        tableTaiLieu.scrollTo(taiLieuMoi);
        int newIndex = danhSachTaiLieu.size() - 1;
        tableTaiLieu.getSelectionModel().select(newIndex);
        tableTaiLieu.getFocusModel().focus(newIndex, colCategory);
    }

    @FXML
    private void handleLuuPhieu() {
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng Grn
            Grn grn = Grn.builder()
                    .receiptId(txtReceiptId.getText().trim())
                    .supplier(txtSupplier.getText().trim())
                    .receiver(txtReceiver.getText().trim())
                    .deliverer(txtDeliverer.getText().trim())
                    .receiveDate(dpReceiveDate.getValue())
                    .build();

            // ✅ Chuyển đổi UI model sang Entity model (THÊM PUBLISHER)
            for (TaiLieuNhapUI uiItem : danhSachTaiLieu) {
                GrnDetail detail = GrnDetail.builder()
                        .category(uiItem.getCategory())
                        .title(uiItem.getTitle())
                        .author(uiItem.getAuthor())
                        .publisher(uiItem.getPublisher())  // ✅ THÊM DÒNG NÀY
                        .publicationYear(uiItem.getPublicationYear())
                        .shelfLocation(uiItem.getShelfLocation())
                        .availableCopies(uiItem.getAvailableCopies())
                        .coverPrice(uiItem.getCoverPrice())
                        .dkcbCode(uiItem.getDkcbCode())
                        .build();

                grn.addItem(detail);
            }

            // Lưu vào database
            grnService.saveGrn(grn);

            // Thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Phiếu nhập kho đã được lưu thành công!\n" +
                    "Mã phiếu: " + grn.getReceiptId() + "\n" +
                    "Số lượng tài liệu: " + danhSachTaiLieu.size());
            alert.showAndWait();

            // Đóng cửa sổ
            clearAllData();
            Stage stage = (Stage) btnLuu.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu phiếu nhập kho");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu phiếu nhập kho");
            alert.setContentText("Lỗi: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (txtReceiptId.getText() == null || txtReceiptId.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập mã hoá đơn\n");
        }

        if (txtSupplier.getText() == null || txtSupplier.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập đơn vị cung cấp\n");
        }

        if (txtReceiver.getText() == null || txtReceiver.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên bên nhận\n");
        }

        if (txtDeliverer.getText() == null || txtDeliverer.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên bên giao\n");
        }

        if (dpReceiveDate.getValue() == null) {
            errors.append("- Vui lòng chọn ngày nhận\n");
        }

        if (danhSachTaiLieu.isEmpty()) {
            errors.append("- Vui lòng thêm ít nhất một tài liệu\n");
        } else {
            for (int i = 0; i < danhSachTaiLieu.size(); i++) {
                TaiLieuNhapUI tl = danhSachTaiLieu.get(i);
                if (tl.getTitle() == null || tl.getTitle().trim().isEmpty()) {
                    errors.append("- Tài liệu dòng " + (i + 1) + ": Thiếu nhan đề\n");
                }
                if (tl.getAvailableCopies() <= 0) {
                    errors.append("- Tài liệu dòng " + (i + 1) + ": Số lượng phải lớn hơn 0\n");
                }
                if (tl.getShelfLocation() == null || tl.getShelfLocation().trim().isEmpty()) {
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

    @FXML
    private void handleHuy() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Huỷ thao tác");
        confirm.setContentText("Bạn có chắc muốn huỷ? Mọi dữ liệu chưa lưu sẽ bị mất.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clearAllData();
            Stage stage = (Stage) btnHuy.getScene().getWindow();
            stage.close();
        }
    }

    private void clearAllData() {
        txtReceiptId.clear();
        txtSupplier.clear();
        txtReceiver.clear();
        txtDeliverer.clear();
        dpReceiveDate.setValue(LocalDate.now());
        danhSachTaiLieu.clear();
    }

    public ObservableList<TaiLieuNhapUI> getDanhSachTaiLieu() {
        return danhSachTaiLieu;
    }

    // ==================== INNER CLASS - UI Model ====================

    /**
     * UI Model cho tài liệu trong bảng (dùng JavaFX Property để binding)
     */
    public static class TaiLieuNhapUI {
        private final StringProperty category = new SimpleStringProperty("");
        private final StringProperty title = new SimpleStringProperty("");
        private final StringProperty author = new SimpleStringProperty("");

        // ✅ THÊM TRƯỜNG MỚI: Nhà xuất bản
        private final StringProperty publisher = new SimpleStringProperty("");

        private final IntegerProperty publicationYear = new SimpleIntegerProperty(LocalDate.now().getYear());
        private final StringProperty shelfLocation = new SimpleStringProperty("");
        private final IntegerProperty availableCopies = new SimpleIntegerProperty(1);
        private final DoubleProperty coverPrice = new SimpleDoubleProperty(0);
        private final StringProperty dkcbCode = new SimpleStringProperty("");

        public TaiLieuNhapUI() {}

        // ===== Getters / Setters / Property methods =====
        public String getCategory() { return category.get(); }
        public void setCategory(String value) { category.set(value); }
        public StringProperty categoryProperty() { return category; }

        public String getTitle() { return title.get(); }
        public void setTitle(String value) { title.set(value); }
        public StringProperty titleProperty() { return title; }

        public String getAuthor() { return author.get(); }
        public void setAuthor(String value) { author.set(value); }
        public StringProperty authorProperty() { return author; }

        // ✅ GETTER/SETTER CHO PUBLISHER
        public String getPublisher() { return publisher.get(); }
        public void setPublisher(String value) { publisher.set(value); }
        public StringProperty publisherProperty() { return publisher; }

        public int getPublicationYear() { return publicationYear.get(); }
        public void setPublicationYear(int value) { publicationYear.set(value); }
        public IntegerProperty publicationYearProperty() { return publicationYear; }

        public String getShelfLocation() { return shelfLocation.get(); }
        public void setShelfLocation(String value) { shelfLocation.set(value); }
        public StringProperty shelfLocationProperty() { return shelfLocation; }

        public int getAvailableCopies() { return availableCopies.get(); }
        public void setAvailableCopies(int value) { availableCopies.set(value); }
        public IntegerProperty availableCopiesProperty() { return availableCopies; }

        public double getCoverPrice() { return coverPrice.get(); }
        public void setCoverPrice(double value) { coverPrice.set(value); }
        public DoubleProperty coverPriceProperty() { return coverPrice; }

        public String getDkcbCode() { return dkcbCode.get(); }
        public void setDkcbCode(String value) { dkcbCode.set(value); }
        public StringProperty dkcbCodeProperty() { return dkcbCode; }
    }
}
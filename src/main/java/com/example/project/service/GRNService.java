package com.example.project.service;

import com.example.project.dto.request.GRNRequest;
import com.example.project.mapper.GRNMapper;
import com.example.project.model.GRN;
import com.example.project.model.GRNDetail;
import com.example.project.model.Librarian;
import com.example.project.model.Supplier;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.GRNRepository;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.SupplierRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GRNService {

    private final GRNRepository grnRepository;
    private final SupplierRepository supplierRepository;
    private final LibrarianRepository librarianRepository;
    private final DocumentRepository documentRepository;
    private final GRNMapper mapper;

    @Transactional
    public GRN create(GRNRequest request) {

        GRN grn = mapper.toEntity(request);

        Librarian librarian = librarianRepository.findById(request.getLibrarianId())
                .orElseThrow();
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow();

        grn.setReceiver(librarian);
        grn.setSupplier(supplier);

        List<GRNDetail> details = request.getDetails().stream()
                .map(d -> {
                    GRNDetail detail = new GRNDetail();
                    detail.setGrn(grn);
                    detail.setDocument(documentRepository.findById(d.getDocumentId()).orElseThrow());
                    detail.setQuantity(d.getQuantity());
                    detail.setUnitPrice(d.getUnitPrice());
                    detail.setNote(d.getNote());
                    return detail;
                }).toList();

        grn.setDetails(details);
        return grnRepository.save(grn);
    }

    public Optional<GRN> findById(Long id) {
        return grnRepository.findById(id);
    }

    public byte[] exportGRNPdf(Long id) {

        Optional<GRN> select = findById(id);
        GRN selected = select.get();

        String html = generateGRNHtml(selected);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            File fontRegular = new File("src/main/resources/fonts/times.ttf");
            builder.useFont(fontRegular, "TIMES NEW ROMAN");

            File fontBold = new File("src/main/resources/fonts/timesbd.ttf");
            builder.useFont(fontBold, "TIMES NEW ROMAN");

            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Xuất file PDF thất bại", e);
        }
    }



    public String generateGRNHtml(GRN grn) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head>");
        html.append("<meta charset=\"UTF-8\" />");
        html.append("<style>");
        html.append("@page { size: A4 landscape; margin: 50pt; }");
        html.append("body { font-family: 'TIMES NEW ROMAN', serif; font-size: 12pt; }");
        html.append("h1 { text-align: center; font-size: 16pt; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20pt; }");
        html.append("th, td { border: 1px solid black; padding: 5px; text-align: center; }");
        html.append("th { font-weight: bold; }");
        html.append("</style>");
        html.append("</head><body>");

        //Khánh tiết
        html.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 20pt;'>");
        html.append("<tbody>");
        html.append("<tr>");
        html.append("<td style='width: 50%; border: none; text-align: center; vertical-align: top; font-size: 14pt;'>");
        html.append("ĐẠI HỌC NGÂN HÀNG<br/>");
        html.append("<strong><u>TRUNG TÂM THÔNG TIN - THƯ VIỆN</u></strong>");
        html.append("</td>");
        html.append("<td style='width: 50%; border: none; text-align: right; vertical-align: top;'>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");

        // Header
        html.append("<h1>PHIẾU NHẬP KHO</h1>");
        html.append("<p style='margin: 4pt 0;'>Theo hóa đơn: ").append(grn.getGrnId()).append("</p>");
        html.append("<p style='margin: 4pt 0;'>Đơn vị cung cấp: ").append(grn.getSupplier().getSupplierName()).append("</p>");
        // Bảng dữ liệu
        html.append("<table>");
        html.append("<tr>")
                .append("<th>STT</th>")
                .append("<th>Bộ sưu tập</th>")
                .append("<th>Nhan đề</th>")
                .append("<th>Tác giả</th>")
                .append("<th>Năm</th>")
                .append("<th>SL</th>")
                .append("<th>Đơn giá</th>")
                .append("<th>Thành tiền</th>")
                .append("<th>Số ĐKCB</th>")
                .append("</tr>");

        int index = 1;
//        for (GRNItem item : grn.getItems()) {
//            html.append("<tr>")
//                    .append("<td>").append(index).append("</td>")
//                    .append("<td>").append(item.getDocument().getCode()).append("</td>")
//                    .append("<td>").append(item.getDocument().getTitle()).append("</td>")
//                    .append("<td>").append(item.getQuantity()).append("</td>")
//                    .append("<td></td>")
//                    .append("</tr>");
//            index++;
//        }

        html.append("<tr>")
                .append("<td>").append(index).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getCategory().getName()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getTitle()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getPublisher()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getPublisher()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getPublisher()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getPublisher()).append("</td>")
                .append("<td>").append(grn.getDetails().get(0).getDocument().getPublisher()).append("</td>")
                .append("<td></td>")
                .append("</tr>");
        index++;
        html.append("</table>");

        String day = String.valueOf(grn.getReceiptDate().getDayOfMonth());
        String month = String.valueOf(grn.getReceiptDate().getMonthValue());
        String year = String.valueOf(grn.getReceiptDate().getYear());

        // Footer
        html.append("<table style='width: 100%; border-collapse: collapse; margin-bottom: 20pt;'>");
        html.append("<tbody>");
        html.append("<tr>");
        html.append("<td style='width: 50%; border: none; text-align: center; vertical-align: top; font-size: 14pt;'>");
        html.append("</td>");
        html.append("<td style='width: 50%; border: none; text-align: center; vertical-align: top; font-size: 14pt;'>");
        html.append("Thành phố Hồ Chí Minh, ngày " + day + " tháng " + month + " năm " + year + "<br/><br/>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</tbody>");
        html.append("</table>");


        html.append("</body></html>");

        return html.toString();
    }

}


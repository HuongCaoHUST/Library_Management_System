package com.example.project.service;

import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.mapper.BorrowSlipMapper;
import com.example.project.model.*;
import com.example.project.repository.*;
import com.example.project.util.SendEmail;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowSlipService {

    private final BorrowSlipRepository borrowSlipRepository;
    private final ReaderRepository readerRepository;
    private final DocumentRepository documentRepository;
    private final BorrowSlipMapper mapper;
    private final SendEmail sendEmail;

    @Transactional
    public BorrowSlipResponse create(BorrowSlipRequest request) {

        BorrowSlip borrowSlip = mapper.toEntity(request);

        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bạn đọc"));

        borrowSlip.setReader(reader);

        List<BorrowSlipDetail> details = request.getDetails().stream()
                .map(d -> {
                    Document document = documentRepository.findById(d.getDocumentId())
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu với id: " + d.getDocumentId()));

                    if (document.getAvailableCopies() < d.getQuantity()) {
                        throw new IllegalArgumentException("Không đủ số lượng tài liệu '" + document.getTitle() + "' cho mượn.");
                    }

                    document.setAvailableCopies(document.getAvailableCopies() - d.getQuantity());
                    document.setBorrowedCopies(document.getBorrowedCopies() + d.getQuantity());
                    documentRepository.save(document);

                    BorrowSlipDetail detail = new BorrowSlipDetail();
                    detail.setBorrowSlip(borrowSlip);
                    detail.setDocument(document);
                    detail.setQuantity(d.getQuantity());
                    return detail;
                }).toList();

        borrowSlip.setDetails(details);
        BorrowSlip saved = borrowSlipRepository.save(borrowSlip);

        // Gửi email sau khi tạo phiếu mượn thành công
        sendBorrowSlipEmail(saved);

        return mapper.toResponse(saved);
    }

    private void sendBorrowSlipEmail(BorrowSlip borrowSlip) {
        String to = "huongcao.seee@gmail.com";
        String subject = "Thông tin chi tiết phiếu mượn";
        String htmlContent = buildBorrowSlipEmailContent(borrowSlip);
        try {
            sendEmail.sendHtmlMail(to, subject, htmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildBorrowSlipEmailContent(BorrowSlip borrowSlip) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Thông tin chi tiết phiếu mượn</h2>");
        html.append("<p><strong>Mã phiếu mượn:</strong> ").append(borrowSlip.getBorrowSlipId()).append("</p>");
        html.append("<p><strong>Bạn đọc:</strong> ").append(borrowSlip.getReader().getFullName()).append("</p>");
        html.append("<p><strong>Ngày mượn:</strong> ").append(borrowSlip.getBorrowDate()).append("</p>");
        html.append("<p><strong>Ngày hết hạn:</strong> ").append(borrowSlip.getDueDate()).append("</p>");
        html.append("<h3>Danh sách tài liệu mượn:</h3>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<thead><tr><th>Tên tài liệu</th><th>Số lượng</th></tr></thead>");
        html.append("<tbody>");
        for (BorrowSlipDetail detail : borrowSlip.getDetails()) {
            html.append("<tr>");
            html.append("<td>").append(detail.getDocument().getTitle()).append("</td>");
            html.append("<td>").append(detail.getQuantity()).append("</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table>");
        html.append("</body></html>");
        return html.toString();
    }
}

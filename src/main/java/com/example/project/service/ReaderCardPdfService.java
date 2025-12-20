package com.example.project.service;

import com.example.project.model.Reader;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReaderCardPdfService {

    private final QrCodeService qrCodeService;

    public byte[] exportReaderCard(Reader reader) {
        try (
                InputStream template =
                    new ClassPathResource("/templates/id_card.pdf").getInputStream();
                PDDocument document = PDDocument.load(template);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {

            PDPage page = document.getPage(0);

            // Generate QR
            String qrContent = reader.getUserId().toString();
            BufferedImage qrImage = qrCodeService.generateQrImage(qrContent, 200);

            PDImageXObject qr =
                    LosslessFactory.createFromImage(document, qrImage);

            // Add text
            PDPageContentStream cs =
                    new PDPageContentStream(
                            document,
                            page,
                            PDPageContentStream.AppendMode.APPEND,
                            true
                    );

            PDType0Font font = PDType0Font.load(document, new ClassPathResource("/fonts/Roboto-Bold.ttf").getInputStream());

            cs.setFont(font, 12);
            cs.setNonStrokingColor(33, 51, 104);
            cs.beginText();
            cs.newLineAtOffset(84, 73);
            cs.showText(reader.getFullName().toUpperCase());
            cs.endText();

            cs.setFont(font, 8);
            cs.beginText();
            cs.newLineAtOffset(27, 11);
            cs.showText(reader.getUserId().toString());
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(84, 47);
            cs.showText(reader.getBirthDate().toString());
            cs.endText();

            LocalDate date = reader.getExpirationDate().toLocalDate();
            cs.beginText();
            cs.newLineAtOffset(84, 22);
            cs.showText(date.toString());
            cs.endText();

            // Add QR
            cs.drawImage(qr, 180, 10, 60, 60);

            cs.close();

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Export PDF thất bại", e);
        }
    }
}

package vn.hub.mailservice.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hub.mailservice.service.EmailService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email HTML đã được gửi thành công đến {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email HTML: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email", e);
        }
    }

    @Override
    public void sendHtmlEmailWithCC(String to, List<String> cc, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setCc(cc.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email HTML với CC đã được gửi thành công đến {} và CC: {}", to, cc);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email HTML với CC: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email với CC", e);
        }
    }

    @Override
    public void sendHtmlEmailWithBCC(String to, List<String> bcc, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setBcc(bcc.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email HTML với BCC đã được gửi thành công đến {} và BCC: {}", to, bcc);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email HTML với BCC: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email với BCC", e);
        }
    }

    @Override
    public void sendEmailWithQRCode(String to, String subject, String htmlContent, String qrCodeContent)
            throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Tạo QR code
            byte[] qrCodeImage = generateQRCodeImage(qrCodeContent, 250, 250);

            // Đính kèm QR code vào email
            helper.addInline("qrcode", new ByteArrayResource(qrCodeImage), "image/png");

            mailSender.send(message);
            log.info("Email với QR code đã được gửi thành công đến {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email với QR code: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email với QR code", e);
        }
    }

    @Override
    public void sendEmailWithPdfAttachment(String to, String subject, String htmlContent, File pdfFile)
            throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Đính kèm file PDF
            FileSystemResource file = new FileSystemResource(pdfFile);
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            log.info("Email với file PDF đính kèm đã được gửi thành công đến {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email với file PDF đính kèm: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email với file PDF đính kèm", e);
        }
    }

    @Override
    public void sendEmailWithPdfAttachment(String to, String subject, String htmlContent, MultipartFile pdfFile)
            throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Lưu file tạm thời
            Path tempFile = Files.createTempFile("attachment", ".pdf");
            pdfFile.transferTo(tempFile.toFile());

            // Đính kèm file PDF
            FileSystemResource file = new FileSystemResource(tempFile.toFile());
            helper.addAttachment(pdfFile.getOriginalFilename(), file);

            mailSender.send(message);

            // Xóa file tạm
            Files.deleteIfExists(tempFile);

            log.info("Email với file PDF upload đã được gửi thành công đến {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email với file PDF upload: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email với file PDF upload", e);
        }
    }

    /**
     * Phương thức tạo QR code
     */
    private byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}
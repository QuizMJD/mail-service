package vn.hub.mailservice.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hub.mailservice.service.EmailService;
import vn.hub.mailservice.util.TemplateUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final TemplateUtil templateUtil;

    /**
     * Gửi thông báo đơn giản cho một người
     */
    public void sendSimpleNotification(String email, String name, String message, String actionUrl) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("message", message);
        variables.put("actionUrl", actionUrl);

        String htmlContent = templateUtil.processTemplate("templates/notification-email.html", variables);
        emailService.sendHtmlEmail(email, "Thông báo mới", htmlContent);
    }

    /**
     * Gửi thông báo cho nhiều người sử dụng CC
     */
    public void sendNotificationToMultipleRecipients(String mainEmail, List<String> ccEmails, String message) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Quý khách");
        variables.put("message", message);
        variables.put("actionUrl", "https://example.com");

        String htmlContent = templateUtil.processTemplate("templates/notification-email.html", variables);
        emailService.sendHtmlEmailWithCC(mainEmail, ccEmails, "Thông báo mới", htmlContent);
    }

    /**
     * Gửi thông báo bí mật cho nhiều người sử dụng BCC
     */
    public void sendConfidentialNotification(String mainEmail, List<String> bccEmails, String message) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Quý khách");
        variables.put("message", message);
        variables.put("actionUrl", "https://example.com/confidential");

        String htmlContent = templateUtil.processTemplate("templates/notification-email.html", variables);
        emailService.sendHtmlEmailWithBCC(mainEmail, bccEmails, "Thông báo bảo mật", htmlContent);
    }

    /**
     * Gửi yêu cầu thanh toán với mã QR
     */
    public void sendPaymentRequest(String email, String customerName, String orderId,
            String serviceName, double amount, LocalDate dueDate,
            String paymentUrl, String qrCodeContent) throws MessagingException {

        Map<String, String> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("orderId", orderId);
        variables.put("serviceName", serviceName);
        variables.put("amount", String.format("%,.0f", amount));
        variables.put("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        variables.put("paymentUrl", paymentUrl);

        String htmlContent = templateUtil.processTemplate("templates/payment-email.html", variables);
        emailService.sendEmailWithQRCode(email, "Yêu cầu thanh toán", htmlContent, qrCodeContent);
    }

    /**
     * Gửi tài liệu kèm theo file PDF
     */
    public void sendDocumentWithAttachment(String email, String name, String documentName, File pdfFile)
            throws MessagingException {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("message", "Chúng tôi gửi đến bạn tài liệu " + documentName + " như đã yêu cầu.");
        variables.put("actionUrl", "https://example.com/documents");

        String htmlContent = templateUtil.processTemplate("templates/notification-email.html", variables);
        emailService.sendEmailWithPdfAttachment(email, "Tài liệu " + documentName, htmlContent, pdfFile);
    }

    /**
     * Gửi tài liệu từ file upload
     */
    public void sendDocumentFromUpload(String email, String name, String documentName, MultipartFile pdfFile)
            throws MessagingException {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("message", "Chúng tôi gửi đến bạn tài liệu " + documentName + " như đã yêu cầu.");
        variables.put("actionUrl", "https://example.com/documents");

        String htmlContent = templateUtil.processTemplate("templates/notification-email.html", variables);
        emailService.sendEmailWithPdfAttachment(email, "Tài liệu " + documentName, htmlContent, pdfFile);
    }
}
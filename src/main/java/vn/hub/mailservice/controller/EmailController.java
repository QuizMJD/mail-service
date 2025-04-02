package vn.hub.mailservice.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hub.mailservice.dto.EmailRequest;
import vn.hub.mailservice.dto.MultipleRecipientsEmailRequest;
import vn.hub.mailservice.dto.QRCodeEmailRequest;
import vn.hub.mailservice.service.EmailService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-html")
    public ResponseEntity<Map<String, String>> sendHtmlEmail(@RequestBody EmailRequest request) {
        emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getHtmlContent());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email HTML đã được gửi thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-with-cc")
    public ResponseEntity<Map<String, String>> sendHtmlEmailWithCC(
            @RequestBody MultipleRecipientsEmailRequest request) {
        emailService.sendHtmlEmailWithCC(request.getTo(), request.getCcList(), request.getSubject(),
                request.getHtmlContent());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email với CC đã được gửi thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-with-bcc")
    public ResponseEntity<Map<String, String>> sendHtmlEmailWithBCC(
            @RequestBody MultipleRecipientsEmailRequest request) {
        emailService.sendHtmlEmailWithBCC(request.getTo(), request.getBccList(), request.getSubject(),
                request.getHtmlContent());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email với BCC đã được gửi thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-with-qrcode")
    public ResponseEntity<Map<String, String>> sendEmailWithQRCode(@RequestBody QRCodeEmailRequest request)
            throws MessagingException {
        emailService.sendEmailWithQRCode(request.getTo(), request.getSubject(), request.getHtmlContent(),
                request.getQrCodeContent());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email với QR code đã được gửi thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-with-pdf")
    public ResponseEntity<Map<String, String>> sendEmailWithPdf(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("htmlContent") String htmlContent,
            @RequestParam("pdfFile") MultipartFile pdfFile) throws MessagingException {

        emailService.sendEmailWithPdfAttachment(to, subject, htmlContent, pdfFile);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email với file PDF đã được gửi thành công");
        return ResponseEntity.ok(response);
    }
}
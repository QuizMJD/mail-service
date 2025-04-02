package vn.hub.mailservice.service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmailService {

    /**
     * Gửi email HTML cho một người nhận
     * 
     * @param to          email người nhận
     * @param subject     tiêu đề email
     * @param htmlContent nội dung HTML
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * Gửi email sử dụng CC cho nhiều người nhận
     * 
     * @param to          email người nhận chính
     * @param cc          danh sách email CC
     * @param subject     tiêu đề email
     * @param htmlContent nội dung HTML
     */
    void sendHtmlEmailWithCC(String to, List<String> cc, String subject, String htmlContent);

    /**
     * Gửi email sử dụng BCC cho nhiều người nhận
     * 
     * @param to          email người nhận chính
     * @param bcc         danh sách email BCC
     * @param subject     tiêu đề email
     * @param htmlContent nội dung HTML
     */
    void sendHtmlEmailWithBCC(String to, List<String> bcc, String subject, String htmlContent);

    /**
     * Gửi email với mã QR code thanh toán
     * 
     * @param to            email người nhận
     * @param subject       tiêu đề email
     * @param htmlContent   nội dung HTML
     * @param qrCodeContent nội dung được mã hóa trong QR code
     */
    void sendEmailWithQRCode(String to, String subject, String htmlContent, String qrCodeContent)
            throws MessagingException;

    /**
     * Gửi email với file PDF đính kèm
     * 
     * @param to          email người nhận
     * @param subject     tiêu đề email
     * @param htmlContent nội dung HTML
     * @param pdfFile     file PDF đính kèm
     */
    void sendEmailWithPdfAttachment(String to, String subject, String htmlContent, File pdfFile)
            throws MessagingException;

    /**
     * Gửi email với file PDF upload từ MultipartFile
     * 
     * @param to          email người nhận
     * @param subject     tiêu đề email
     * @param htmlContent nội dung HTML
     * @param pdfFile     file PDF từ form upload
     */
    void sendEmailWithPdfAttachment(String to, String subject, String htmlContent, MultipartFile pdfFile)
            throws MessagingException;
}
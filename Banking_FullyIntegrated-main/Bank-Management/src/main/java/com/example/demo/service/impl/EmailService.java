package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendLoginConfirmationEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("🛡️ FEINBank - Login Alert");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f5f5f5;'>"
                    + "<div style='max-width: 600px; margin: auto; background: #ffffff; border-radius: 10px; padding: 20px;'>"
                    + "<h2 style='color: #8D1B3D;'>Welcome back, " + name + "!</h2>"
                    + "<p style='font-size: 16px;'>🎉 You have successfully logged into your <strong>FEINBank</strong> account.</p>"
                    + "<p>If this wasn't you, please <a href='#' style='color: #d9534f;'></a> contact our support team immediately.</p>"
                    + "<hr style='margin: 20px 0;'/>"
                    + "<p style='font-size: 14px; color: #777;'>This is an automated message from VaultMaster. Please do not reply.</p>"
                    + "</div></div>";

            helper.setText(htmlContent, true); // Enable HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send login email", e);
        }
}
    @Value("${spring.mail.username}")
    private String fromEmail;
        public void sendAccountApprovalEmail(String toEmail, String customerName, 
                                           String customerId, String loginPassword, 
                                           String transactionPassword, String remarks) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Account Approved - Welcome to Fein Bank");

                String htmlContent = buildApprovalEmailTemplate(customerName, customerId, 
                                                               loginPassword, transactionPassword, remarks);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                System.out.println("Approval email sent successfully to: " + toEmail);
            } catch (MessagingException e) {
                System.err.println("Failed to send approval email: " + e.getMessage());
                throw new RuntimeException("Email sending failed", e);
            }
        }

        public void sendAccountRejectionEmail(String toEmail, String customerName, String remarks) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Account Application Status - Update Required");

                String htmlContent = buildRejectionEmailTemplate(customerName, remarks);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                System.out.println("Rejection email sent successfully to: " + toEmail);
            } catch (MessagingException e) {
                System.err.println("Failed to send rejection email: " + e.getMessage());
                throw new RuntimeException("Email sending failed", e);
            }
        }

        private String buildApprovalEmailTemplate(String customerName, String customerId, 
                                                String loginPassword, String transactionPassword, 
                                                String remarks) {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .credentials { background-color: #e8f5e8; padding: 15px; border-left: 4px solid #27ae60; margin: 15px 0; }
                        .warning { background-color: #fff3cd; padding: 10px; border: 1px solid #ffeaa7; border-radius: 4px; margin: 15px 0; }
                        .footer { text-align: center; padding: 20px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Account Approved!</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>
                            <p>Congratulations! Your bank account application has been approved.</p>
                            
                            <div class="credentials">
                                <h3>Your Account Details:</h3>
                                <p><strong>Customer ID:</strong> %s</p>
                                <p><strong>Login Password:</strong> %s</p>
                                <p><strong>Transaction Password:</strong> %s</p>
                            </div>
                            
                            %s
                            
                            <p>Welcome to our banking family! You can now access your account through our online banking portal.</p>
                            
                            <p>If you have any questions, please contact our customer service.</p>
                            
                            <p>Best regards,<br>
                            Banking Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(customerName, customerId, loginPassword, transactionPassword, 
                             remarks != null && !remarks.trim().isEmpty() ? 
                             "<p><strong>Admin Remarks:</strong> " + remarks + "</p>" : "");
        }

        private String buildRejectionEmailTemplate(String customerName, String remarks) {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #e74c3c; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .remarks { background-color: #ffeaa7; padding: 15px; border-left: 4px solid #f39c12; margin: 15px 0; }
                        .footer { text-align: center; padding: 20px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Account Application Update</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>
                            <p>Thank you for your interest in opening an account with us.</p>
                            <p>After careful review, we regret to inform you that your account application requires additional attention.</p>
                            
                            %s
                            
                            <p>Please feel free to contact our customer service team if you would like to discuss this decision or reapply with additional documentation.</p>
                            
                            <p>We appreciate your understanding.</p>
                            
                            <p>Best regards,<br>
                            Banking Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(customerName, 
                             remarks != null && !remarks.trim().isEmpty() ? 
                             "<div class=\"remarks\"><strong>Reason:</strong> " + remarks + "</div>" : "");
        }
    
	
}

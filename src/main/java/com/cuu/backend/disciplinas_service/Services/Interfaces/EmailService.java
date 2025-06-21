package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Entities.PaymentProof;
import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendSimpleEmail(String to, String subject, String text);
    public void sendPaymentReceiptEmail(Fee fee, PaymentProof paymentProof) throws MessagingException;
    public void sendAccountDeletionConfirmationEmail(String email, String fullName) throws MessagingException;
}

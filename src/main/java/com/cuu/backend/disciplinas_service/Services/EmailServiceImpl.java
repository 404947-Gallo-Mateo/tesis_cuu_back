package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Entities.PaymentProof;
import com.cuu.backend.disciplinas_service.Models.Enums.PaymentType;
import com.cuu.backend.disciplinas_service.Services.Interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER_EMAIL);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendPaymentReceiptEmail(Fee fee, PaymentProof paymentProof) throws MessagingException {
        String subject = "Comprobante de pago - " + fee.getDescription();
        String htmlContent = generatePaymentReceiptHtml(fee, paymentProof);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(fee.getPayerEmail());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generatePaymentReceiptHtml(Fee fee, PaymentProof paymentProof) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Comprobante de Pago</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { max-width: 150px; margin-bottom: 20px; }
                    h1 { color: #2c3e50; font-size: 24px; margin-bottom: 20px; }
                    .receipt { border: 1px solid #ddd; border-radius: 5px; padding: 20px; margin-bottom: 30px; }
                    .receipt-details { margin-bottom: 20px; }
                    .receipt-row { display: flex; justify-content: space-between; margin-bottom: 10px; }
                    .receipt-label { font-weight: bold; color: #555; }
                    .receipt-value { text-align: right; }
                    .total { font-size: 18px; font-weight: bold; border-top: 2px solid #eee; padding-top: 10px; margin-top: 10px; }
                    .footer { text-align: center; margin-top: 30px; color: #777; font-size: 14px; }
                    .status-approved { color: #27ae60; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Comprobante de Pago</h1>
                </div>
                
                <div class="receipt">
                    <div class="receipt-details">
                        <div class="receipt-row">
                            <span class="receipt-label">Número de transacción:</span>
                            <span class="receipt-value">""" + paymentProof.getId() + """
                        </span>
                        </div>
                        <div class="receipt-row">
                            <span class="receipt-label">Fecha de pago:</span>
                            <span class="receipt-value">""" + paymentProof.getPaymentDate().format(dateTimeFormatter) + """
                        </span>
                        </div>
                        <div class="receipt-row">
                            <span class="receipt-label">Método de pago:</span>
                            <span class="receipt-value">""" + getPaymentMethodName(paymentProof.getPaymentMethod()) + """
                        </span>
                        </div>
                        <div class="receipt-row">
                            <span class="receipt-label">Estado:</span>
                            <span class="receipt-value status-approved">APROBADO</span>
                        </div>
                    </div>
                    
                    <div class="receipt-details">
                        <div class="receipt-row">
                            <span class="receipt-label">Concepto:</span>
                            <span class="receipt-value">""" + fee.getDescription() + """
                        </span>
                        </div>
                        <div class="receipt-row">
                            <span class="receipt-label">Período:</span>
                            <span class="receipt-value">""" + fee.getPeriod().toString() + """
                        </span>
                        </div>
                        <div class="receipt-row">
                            <span class="receipt-label">Fecha de vencimiento:</span>
                            <span class="receipt-value">""" + fee.getDueDate().format(dateFormatter) + """
                        </span>
                        </div>
                        <div class="receipt-row total">
                            <span class="receipt-label">Total pagado:</span>
                            <span class="receipt-value">$""" + fee.getAmount() + """
                        </span>
                        </div>
                    </div>
                </div>
                
                <div class="footer">
                    <p>Gracias por su pago. Este comprobante es válido como justificante de pago.</p>
                    <p>Club Unión Unquillo - Todos los derechos reservados</p>
                    <p>Para cualquier consulta, contacte con club.union.unquillo.mail@gmail.com</p>
                </div>
            </body>
            </html>
            """;
    }

    private String getPaymentMethodName(PaymentType paymentType) {
        if (paymentType == null) return "No especificado";
        switch (paymentType) {
            case CASH: return "Efectivo";
            case CREDIT_CARD: return "Tarjeta de crédito";
            case DEBIT_CARD: return "Tarjeta de débito";
            case MERCADO_PAGO: return "Mercado Pago";
            default: return paymentType.toString();
        }
    }

    public void sendAccountDeletionConfirmationEmail(String email, String fullName) throws MessagingException {
        String subject = "Confirmación de eliminación de cuenta";
        String htmlContent = generateAccountDeletionConfirmationHtml(fullName);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generateAccountDeletionConfirmationHtml(String fullName) {
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cuenta eliminada</title>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { text-align: center; margin-bottom: 30px; }
                .logo { max-width: 150px; margin-bottom: 20px; }
                h1 { color: #2c3e50; font-size: 24px; margin-bottom: 20px; }
                .content { margin-bottom: 30px; }
                .footer { text-align: center; margin-top: 30px; color: #777; font-size: 14px; }
                .highlight { background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>Confirmación de eliminación de cuenta</h1>
            </div>
            
            <div class="content">
                <p>Hola""" + fullName + """
                ,</p>
                
                <p>Queremos informarte que tu cuenta en el Club Unión Unquillo ha sido eliminada de nuestros sistemas.</p>
                
                <div class="highlight">
                    <p><strong>¿Qué significa esto?</strong></p>
                    <ul>
                        <li>Todos tus datos personales han sido eliminados de nuestra base de datos</li>
                        <li>Ya no tendrás acceso a los servicios del Club</li>
                        <li>Si tenias cuotas sin pagar, estas no se han cancelado, debes pagarlas</li>
                    </ul>
                </div>
                
                <p>Si crees que esto es un error o no solicitaste la eliminación de tu cuenta, por favor contacta con nuestro equipo de soporte inmediatamente.</p>
            </div>
            
            <div class="footer">
                <p>Club Unión Unquillo - Todos los derechos reservados</p>
                <p>Para cualquier consulta, contacte con club.union.unquillo.mail@gmail.com</p>
                <p>©""" + Year.now().getValue() + """
             - Club Unión Unquillo</p>
            </div>
        </body>
        </html>
        """;
    }
}

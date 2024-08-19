package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.exceptions.EmailSendingException;
import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.services.report.ReportService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DefaultEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final ReportService reportService;

    public DefaultEmailService(JavaMailSender mailSender, ReportService reportService) {
        this.mailSender = mailSender;
        this.reportService = reportService;
    }

    @Override
    public void sendLowStockAlert(List<Product> products) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo("contatomateusgd@gmail.com");
            messageHelper.setSubject("Alerta de Estoque Baixo");

            String emailContent = buildEmailContent(products);
            messageHelper.setText(emailContent, true);

            addAttachmentsToMessageHelper(products, messageHelper);

            mailSender.send(mimeMessage);
        } catch (EmailSendingException e) {
            throw new EmailSendingException("Erro ao enviar o alerta de estoque baixo. Verifique os detalhes dos produtos.", e);
        } catch (IOException e) {
            throw new EmailSendingException("Erro ao processar os relatórios anexos do alerta de estoque baixo.", e);
        } catch (MessagingException e) {
            throw new EmailSendingException("Erro ao criar o e-mail com anexos.", e);
        } catch (Exception e) {
            throw new EmailSendingException("Ocorreu um erro inesperado ao enviar o alerta de estoque baixo.", e);
        }
    }

    @Override
    public void sendStockReplenishmentAlert(String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo("contatomateusgd@gmail.com");
            messageHelper.setSubject("Alerta de Reabastecimento de Estoque");
            messageHelper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSendingException("Erro ao enviar o alerta de reabastecimento de estoque.", e);
        } catch (Exception e) {
            throw new EmailSendingException("Ocorreu um erro inesperado ao enviar o alerta de reabastecimento de estoque.", e);
        }
    }

    private void addAttachmentsToMessageHelper(List<Product> products, MimeMessageHelper messageHelper) throws IOException, MessagingException {
        ByteArrayInputStream pdfReportStream = reportService.generatePdfLowStockReport(products);
        ByteArrayResource pdfReportResource = new ByteArrayResource(pdfReportStream.readAllBytes()) {
            @Override
            public String getFilename() {
                return "Relatório_Estoque_Baixo.pdf";
            }
        };
        messageHelper.addAttachment(pdfReportResource.getFilename(), pdfReportResource);

        ByteArrayInputStream excelReportStream = reportService.generateExcelLowStockReport(products);
        ByteArrayResource excelReportResource = new ByteArrayResource(excelReportStream.readAllBytes()) {
            @Override
            public String getFilename() {
                return "Relatório_Estoque_Baixo.xlsx";
            }
        };
        messageHelper.addAttachment(excelReportResource.getFilename(), excelReportResource);
    }

    private String buildEmailContent(List<Product> products) throws IOException {
        String filePath = "src/main/resources/templates/low_stock_alert.html"; // Caminho do template HTML
        String emailContent = new String(Files.readAllBytes(Paths.get(filePath)));

        StringBuilder tableRows = new StringBuilder();

        for (Product product : products) {
            tableRows.append("<tr>")
                    .append("<td>").append(product.getProductName() != null ? product.getProductName() : "N/A").append("</td>")
                    .append("<td>").append(product.getDescription() != null ? product.getDescription() : "N/A").append("</td>")
                    .append("<td>").append(product.getBrand() != null ? product.getBrand() : "N/A").append("</td>")
                    .append("<td>").append(product.getCategory() != null ? product.getCategory().toString() : "N/A").append("</td>")
                    .append("<td>").append(String.format("%.2f", product.getPurchasePrice())).append("</td>")
                    .append("<td>").append(String.format("%.2f", product.getSalePrice())).append("</td>")
                    .append("<td>").append(product.getStockQuantity()).append("</td>")
                    .append("<td>").append(product.getMinimumQuantity()).append("</td>")
                    .append("</tr>");
        }

        emailContent = emailContent.replace("<!-- Dados dos produtos serão inseridos aqui -->", tableRows.toString());
        return emailContent;
    }
}
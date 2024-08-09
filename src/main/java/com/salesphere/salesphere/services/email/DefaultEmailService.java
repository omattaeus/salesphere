package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.exceptions.EmailSendingException;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.services.report.ReportService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    private String buildEmailContent(List<Product> products) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                .append(".container { width: 80%; margin: auto; padding: 20px; background: #fff; border-radius: 5px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                .append("h2 { color: #333; }")
                .append("p { font-size: 1em; color: #555; }")
                .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #f2f2f2; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h2>Relatório de Estoque Baixo</h2>")
                .append("<p>Segue em anexo o relatório de produtos com estoque baixo.</p>")
                .append("<table>")
                .append("<tr>")
                .append("<th>Nome do Produto</th>")
                .append("<th>Descrição</th>")
                .append("<th>Marca</th>")
                .append("<th>Categoria</th>")
                .append("<th>Preço de Compra</th>")
                .append("<th>Preço de Venda</th>")
                .append("<th>Quantidade em Estoque</th>")
                .append("<th>Quantidade Mínima</th>")
                .append("</tr>");

        for (Product product : products) {
            sb.append("<tr>")
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

        sb.append("</table>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return sb.toString();
    }
}
package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.models.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public DefaultEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendLowStockAlert(List<Product> products) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setTo("contatomateusgd@gmail.com");
            messageHelper.setSubject("Alerta de Estoque Baixo");

            String emailContent = buildEmailContent(products);
            messageHelper.setText(emailContent, true); // 'true' indicates HTML content

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildEmailContent(List<Product> products) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }")
                .append(".container { width: 80%; margin: auto; padding: 20px; background: #fff; border-radius: 5px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }")
                .append("h2 { color: #333; }")
                .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                .append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }")
                .append("th { background-color: #f4f4f4; }")
                .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                .append(".footer { margin-top: 20px; padding: 10px; text-align: center; font-size: 0.9em; color: #777; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h2>Alerta de Estoque Baixo</h2>")
                .append("<p>Os seguintes produtos estão com estoque baixo:</p>")
                .append("<table>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Produto</th>")
                .append("<th>Quantidade em Estoque</th>")
                .append("<th>Quantidade Mínima</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        for (Product product : products) {
            sb.append("<tr>")
                    .append("<td>").append(product.getProductName()).append("</td>")
                    .append("<td>").append(product.getStockQuantity()).append("</td>")
                    .append("<td>").append(product.getMinimumQuantity()).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody>")
                .append("</table>")
                .append("<div class='footer'>")
                .append("<p>Obrigado por acompanhar o estoque.</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return sb.toString();
    }
}
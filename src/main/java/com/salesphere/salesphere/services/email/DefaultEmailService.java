package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.models.Product;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("contatomateusgd@gmail.com");
        message.setSubject("Alerta de Estoque Baixo");

        StringBuilder messageText = new StringBuilder();
        messageText.append("Os seguintes produtos estão com estoque baixo:\n\n");

        for (Product product : products) {
            messageText.append("Produto: ").append(product.getProductName()).append("\n")
                    .append("Quantidade em estoque: ").append(product.getStockQuantity()).append("\n")
                    .append("Quantidade mínima: ").append(product.getMinimumQuantity()).append("\n\n");
        }

        message.setText(messageText.toString());
        mailSender.send(message);
    }
}
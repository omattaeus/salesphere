package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.repositories.ProductRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;
    private final JavaMailSender mailSender;

    @Autowired
    public ProductService(ProductRepository repository, ProductMapper productMapper, JavaMailSender mailSender) {
        this.repository = repository;
        this.productMapper = productMapper;
        this.mailSender = mailSender;
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> allProducts = repository.findAll();
        return allProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO.productName().isEmpty() || productRequestDTO.category() == null) {
            throw new RuntimeException("Product cannot be empty!");
        }
        Product product = productMapper.toProduct(productRequestDTO);
        Product savedProduct = repository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    public List<Product> getProductsWithLowStock() {
        return repository.findProductsWithLowStock();
    }

    public boolean checkStock() {
        List<Product> productsWithLowStock = getProductsWithLowStock();
        if (productsWithLowStock.isEmpty()) {
            return false;
        } else {
            sendLowStockAlert(productsWithLowStock);
            return true;
        }
    }

    public void sendLowStockAlert(List<Product> products) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("qualquercoisa479@gmail.com");
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
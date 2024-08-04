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

    public void checkStock() {
        List<Product> productsWithLowStock = getProductsWithLowStock();
        if (productsWithLowStock.isEmpty()) {
            System.out.println("Nenhum produto com estoque baixo.");
        } else {
            for (Product product : productsWithLowStock) {
                sendLowStockAlert(product);
            }
        }
    }

    public void sendLowStockAlert(Product product) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("qualquercoisa479@gmail.com");
        message.setSubject("Alerta de Estoque Baixo");
        message.setText("O produto " + product.getProductName() + " está com estoque baixo.\n" +
                "Quantidade em estoque: " + product.getStockQuantity() + "\n" +
                "Quantidade mínima: " + product.getMinimumQuantity());

        mailSender.send(message);
    }
}
package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
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
            throw new RuntimeException("O produto não pode estar vazio!");
        }
        Product product = productMapper.toProduct(productRequestDTO);
        Product savedProduct = repository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        try {
            productMapper.updateProductFromDto(productRequestDTO, existingProduct);

            if (existingProduct.getCodeSku() == null || existingProduct.getPurchasePrice() == null) {
                throw new IllegalArgumentException("Campos obrigatórios não preenchidos");
            }

            Product updatedProduct = repository.save(existingProduct);
            return productMapper.toProductResponse(updatedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    @Transactional
    public ProductResponseDTO partialUpdateProduct(Long productId, Map<String, Object> updates) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Product.class, key);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType() == Long.class && value instanceof Integer) {
                    value = ((Integer) value).longValue();
                }
                ReflectionUtils.setField(field, existingProduct, value);
            }
        });

        Product updatedProduct = repository.save(existingProduct);

        return productMapper.toProductResponse(updatedProduct);
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
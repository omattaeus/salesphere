package com.salesphere.salesphere.services.product.stock.validator;

import com.salesphere.salesphere.models.dto.product.request.ProductRequestDTO;
import com.salesphere.salesphere.repositories.product.ProductRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class StockValidator {

    public void validateStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new ValidationException("A quantidade em estoque deve ser um número inteiro não negativo.");
        }
    }

    public void validateMinimumQuantity(int stockQuantity, int minimumQuantity) {
        if (minimumQuantity < 0 || minimumQuantity > stockQuantity) {
            throw new ValidationException("A quantidade mínima deve ser um número inteiro não negativo e menor ou igual à quantidade em estoque.");
        }
    }

    public void validateSKU(String sku, ProductRepository repository) {
        if (repository.existsByCodeSku(sku)) {
            throw new ValidationException("O SKU deve ser único.");
        }
    }

    public void validatePrice(Double price) {
        if (price == null || price <= 0) {
            throw new ValidationException("O preço deve ser um valor numérico positivo.");
        }
    }

    public void validateRequiredFields(ProductRequestDTO dto) {
        if (dto.productName() == null || dto.productName().isEmpty()) {
            throw new ValidationException("O nome do produto é obrigatório.");
        }
        if (dto.description() == null || dto.description().isEmpty()) {
            throw new ValidationException("A descrição do produto é obrigatória.");
        }
        if (dto.brand() == null || dto.brand().isEmpty()) {
            throw new ValidationException("A marca do produto é obrigatória.");
        }
        if (dto.category() == null) {
            throw new ValidationException("A categoria do produto é obrigatória.");
        }
        if (dto.purchasePrice() == null || dto.purchasePrice() <= 0) {
            throw new ValidationException("O preço de compra é obrigatório e deve ser maior que zero.");
        }
        if (dto.salePrice() == null || dto.salePrice() <= 0) {
            throw new ValidationException("O preço de venda é obrigatório e deve ser maior que zero.");
        }
        if (dto.stockQuantity() == null || dto.stockQuantity() < 0) {
            throw new ValidationException("A quantidade em estoque deve ser um número não negativo.");
        }
        if (dto.minimumQuantity() == null || dto.minimumQuantity() < 0) {
            throw new ValidationException("A quantidade mínima deve ser um número não negativo.");
        }
        if (dto.codeSku() == null || dto.codeSku().isEmpty()) {
            throw new ValidationException("O código SKU é obrigatório.");
        }
        if (dto.availability() == null) {
            throw new ValidationException("A disponibilidade do produto é obrigatória.");
        }
    }
}

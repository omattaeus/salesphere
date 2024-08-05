package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.services.email.EmailService;
import com.salesphere.salesphere.services.scheduler.StockCheckStrategy;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements StockCheckStrategy {

    private final ProductRepository repository;
    private final ProductMapper productMapper;
    private final EmailService emailService;

    @Autowired
    public ProductService(ProductRepository repository, ProductMapper productMapper,
                          EmailService emailService) {
        this.repository = repository;
        this.productMapper = productMapper;
        this.emailService = emailService;
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> allProducts = repository.findAll();
        return allProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        validateProductRequest(productRequestDTO);
        Product product = productMapper.toProduct(productRequestDTO);
        Product savedProduct = repository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        productMapper.updateProductFromDto(productRequestDTO, existingProduct);
        validateProduct(existingProduct);

        Product updatedProduct = repository.save(existingProduct);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    public ProductResponseDTO partialUpdateProduct(Long productId, Map<String, Object> updates) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        applyUpdates(existingProduct, updates);

        Product updatedProduct = repository.save(existingProduct);

        return productMapper.toProductResponse(updatedProduct);
    }

    public List<Product> getRawProductsWithLowStock() {
        return repository.findProductsWithLowStock();
    }

    public List<ProductResponseDTO> getProductsWithLowStock() {
        List<Product> productsWithLowStock = repository.findProductsWithLowStock();
        return productsWithLowStock.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void checkStock() {
        List<Product> productsWithLowStock = getRawProductsWithLowStock();
        if (!productsWithLowStock.isEmpty()) {
            emailService.sendLowStockAlert(productsWithLowStock);
        }
    }

    public void deleteProduct(Long productId) {
        if (!repository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        repository.deleteById(productId);
    }

    private void validateProductRequest(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO.productName().isEmpty() || productRequestDTO.category() == null) {
            throw new ValidationException("O produto não pode estar vazio!");
        }
    }

    private void validateProduct(Product product) {
        if (product.getCodeSku() == null || product.getPurchasePrice() == null) {
            throw new IllegalArgumentException("Campos obrigatórios não preenchidos");
        }
    }

    private void applyUpdates(Product product, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Product.class, key);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType() == Long.class && value instanceof Integer) {
                    value = ((Integer) value).longValue();
                }
                ReflectionUtils.setField(field, product, value);
            }
        });
    }
}
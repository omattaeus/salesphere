package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.repositories.AvailabilityRepository;
import com.salesphere.salesphere.services.converter.ProductUpdater;
import com.salesphere.salesphere.services.email.EmailService;
import com.salesphere.salesphere.services.scheduler.StockCheckStrategy;
import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements StockCheckStrategy {

    private final ProductRepository repository;
    private final AvailabilityRepository availabilityRepository;
    private final ProductMapper productMapper;
    private final ProductUpdater productUpdater;
    private final EmailService emailService;
    private final StockWebSocketHandler stockWebSocketHandler;

    public ProductService(ProductRepository repository, AvailabilityRepository availabilityRepository,
                          ProductMapper productMapper, ProductUpdater productUpdater,
                          EmailService emailService, StockWebSocketHandler stockWebSocketHandler) {
        this.repository = repository;
        this.availabilityRepository = availabilityRepository;
        this.productMapper = productMapper;
        this.productUpdater = productUpdater;
        this.emailService = emailService;
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> allProducts = repository.findAll();
        return allProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> createProducts(List<ProductRequestDTO> productRequestDTOs) {
        productRequestDTOs.forEach(this::validateProductRequest);

        return productRequestDTOs.stream()
                .map(productRequestDTO -> {
                    Product product = productMapper.toProduct(productRequestDTO);
                    Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                            .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
                    product.setAvailability(availability);
                    Product savedProduct = repository.save(product);
                    return productMapper.toProductResponse(savedProduct);
                })
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        validateProductRequest(productRequestDTO);
        Product product = productMapper.toProduct(productRequestDTO);
        Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
        product.setAvailability(availability);
        Product savedProduct = repository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        productMapper.updateProductFromDto(productRequestDTO, existingProduct);
        validateProduct(existingProduct);

        Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
        existingProduct.setAvailability(availability);

        Product updatedProduct = repository.save(existingProduct);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    public ProductResponseDTO partialUpdateProduct(Long productId, Map<String, Object> updates) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        productUpdater.applyUpdates(existingProduct, updates);

        Product updatedProduct = repository.save(existingProduct);

        emailService.sendLowStockAlert(List.of(updatedProduct));

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
    @Scheduled(cron = "0 0 * * * *")
    public void checkStock() {
        List<Product> productsWithLowStock = getRawProductsWithLowStock();
        if (!productsWithLowStock.isEmpty()) {
            emailService.sendLowStockAlert(productsWithLowStock);
            String message = createStockUpdateMessage(productsWithLowStock);
            try {
                stockWebSocketHandler.sendStockUpdate(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String createStockUpdateMessage(List<Product> productsWithLowStock) {
        return "Produtos com estoque baixo: " + productsWithLowStock.stream()
                .map(product -> product.getProductName() + " (Estoque: " + product.getStockQuantity() + ")")
                .collect(Collectors.joining(", "));
    }

    public void deleteProduct(Long productId) {
        if (!repository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        repository.deleteById(productId);
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

    private void validateProductRequest(ProductRequestDTO productRequestDTO) {
        validateProductName(productRequestDTO.productName());
        validateCategory(productRequestDTO.category());
        validatePrices(productRequestDTO.purchasePrice(), productRequestDTO.salePrice());
        validateQuantities(productRequestDTO.stockQuantity(), productRequestDTO.minimumQuantity());
        validateAvailability(productRequestDTO.availability());
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.isEmpty()) {
            throw new ValidationException("O nome do produto é obrigatório!");
        }
    }

    private void validateCategory(CategoryEnum category) {
        if (category == null) {
            throw new ValidationException("A categoria do produto é obrigatória!");
        }
    }

    private void validatePrices(Double purchasePrice, Double salePrice) {
        if (purchasePrice == null || purchasePrice <= 0) {
            throw new ValidationException("O preço de compra é obrigatório e deve ser maior que zero!");
        }
        if (salePrice == null || salePrice <= 0) {
            throw new ValidationException("O preço de venda é obrigatório e deve ser maior que zero!");
        }
    }

    private void validateQuantities(Long stockQuantity, Long minimumQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new ValidationException("A quantidade em estoque é obrigatória e deve ser zero ou positiva!");
        }
        if (minimumQuantity == null || minimumQuantity < 0) {
            throw new ValidationException("A quantidade mínima é obrigatória e deve ser zero ou positiva!");
        }
    }

    private void validateAvailability(AvailabilityEnum availability) {
        if (availability == null) {
            throw new ValidationException("A disponibilidade do produto é obrigatória!");
        }
    }
}
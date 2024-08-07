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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements StockCheckStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

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
                    try {
                        Product product = productMapper.toProduct(productRequestDTO);
                        Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                                .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
                        product.setAvailability(availability);
                        Product savedProduct = repository.save(product);
                        return productMapper.toProductResponse(savedProduct);
                    } catch (ValidationException e) {
                        logger.error("Erro ao criar produto: {}", e.getMessage());
                        throw e;
                    }
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


    public ProductResponseDTO partialUpdateProduct(Long productId, Map<String, Object> updates) {
        Product existingProduct = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Product.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingProduct, value);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo " + key + " não encontrado");
            }
        });

        validateProduct(existingProduct);
        Product updatedProduct = repository.save(existingProduct);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void checkStock() {
        List<Product> productsWithLowStock = getRawProductsWithLowStock();

        if (!productsWithLowStock.isEmpty()) {
            String message = createStockUpdateMessage(productsWithLowStock);

            for (WebSocketSession session : stockWebSocketHandler.getSessions()) {
                try {
                    stockWebSocketHandler.sendMessage(session, message);
                } catch (IOException e) {
                    logger.error("Erro ao enviar mensagem via WebSocket: {}", e.getMessage());
                }
            }

            emailService.sendLowStockAlert(productsWithLowStock);
        } else {
            logger.info("Nenhum produto com estoque baixo encontrado.");
        }
    }

    private String createStockUpdateMessage(List<Product> productsWithLowStock) {
        return productsWithLowStock.stream()
                .map(product -> product.getProductName() + " (Estoque: " + product.getStockQuantity() + ")")
                .collect(Collectors.joining("\n"));
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

    public void deleteProduct(Long productId) {
        try {
            if (!repository.existsById(productId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
            }
            repository.deleteById(productId);
        } catch (ResponseStatusException ex) {
            logger.error("Erro ao deletar produto: {}", ex.getMessage());
            throw ex;
        }
    }

    private void validateProduct(Product product) {
        if (product.getCodeSku() == null || product.getPurchasePrice() == null) {
            throw new IllegalArgumentException("Campos obrigatórios não preenchidos");
        }
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
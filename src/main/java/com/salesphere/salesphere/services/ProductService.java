package com.salesphere.salesphere.services;

import com.salesphere.salesphere.exceptions.ErrorResponse;
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
import org.springframework.http.ResponseEntity;
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

    public ProductResponseDTO getProductById(Long productId) {
        try {
            Product product = repository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
            return productMapper.toProductResponse(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        }
    }

    public List<ProductResponseDTO> getAllProducts() {
        try {
            List<Product> allProducts = repository.findAll();
            return allProducts.stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar produtos.");
        }
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
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar produto: " + e.getMessage());
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar produtos.");
                    }
                })
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        try {
            validateProductRequest(productRequestDTO);
            Product product = productMapper.toProduct(productRequestDTO);
            Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                    .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
            product.setAvailability(availability);
            Product savedProduct = repository.save(product);
            return productMapper.toProductResponse(savedProduct);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar produto: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar produto.");
        }
    }

    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        try {
            Product existingProduct = repository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

            productMapper.updateProductFromDto(productRequestDTO, existingProduct);
            validateProduct(existingProduct);

            Availability availability = availabilityRepository.findByAvailability(productRequestDTO.availability())
                    .orElseThrow(() -> new ValidationException("Disponibilidade não encontrada"));
            existingProduct.setAvailability(availability);

            Product updatedProduct = repository.save(existingProduct);
            return productMapper.toProductResponse(updatedProduct);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao atualizar produto: " + e.getMessage());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar produto.");
        }
    }

    public ProductResponseDTO partialUpdateProduct(Long productId, Map<String, Object> updates) {
        try {
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
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar produto parcialmente.");
        }
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void checkStock() {
        try {
            List<Product> productsWithLowStock = getRawProductsWithLowStock();

            if (!productsWithLowStock.isEmpty()) {
                String message = createStockUpdateMessage(productsWithLowStock);

                for (WebSocketSession session : stockWebSocketHandler.getSessions()) {
                    try {
                        stockWebSocketHandler.sendMessage(session, message);
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar mensagem via WebSocket.");
                    }
                }

                emailService.sendLowStockAlert(productsWithLowStock);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao verificar estoque.");
        }
    }

    private String createStockUpdateMessage(List<Product> productsWithLowStock) {
        return productsWithLowStock.stream()
                .map(product -> product.getProductName() + " (Estoque: " + product.getStockQuantity() + ")")
                .collect(Collectors.joining("\n"));
    }

    public List<Product> getRawProductsWithLowStock() {
        try {
            return repository.findProductsWithLowStock();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar produtos com estoque baixo.");
        }
    }

    public List<ProductResponseDTO> getProductsWithLowStock() {
        try {
            List<Product> productsWithLowStock = repository.findProductsWithLowStock();
            return productsWithLowStock.stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar produtos com estoque baixo.");
        }
    }

    public void deleteProduct(Long productId) {
        try {
            if (!repository.existsById(productId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
            }
            repository.deleteById(productId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao deletar produto.");
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
            throw new ValidationException("A quantidade em estoque deve ser um número positivo ou zero.");
        }
        if (minimumQuantity == null || minimumQuantity < 0) {
            throw new ValidationException("A quantidade mínima deve ser um número positivo ou zero.");
        }
    }

    private void validateAvailability(AvailabilityEnum availability) {
        if (availability == null) {
            throw new ValidationException("A disponibilidade do produto é obrigatória!");
        }
    }
}
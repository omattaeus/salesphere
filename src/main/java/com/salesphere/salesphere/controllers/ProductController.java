package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.exceptions.ErrorResponse;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.dto.ProductSaleDTO;
import com.salesphere.salesphere.services.ProductService;
import com.salesphere.salesphere.services.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/products")
@RestController
public class ProductController {

    private final ProductService productService;
    private final EmailService emailService;

    public ProductController(ProductService productService, EmailService emailService) {
        this.productService = productService;
        this.emailService = emailService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find product by ID", description = "Finds a product by its ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "Internal Error", responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Object> getProductById(@PathVariable("id") Long productId) {
        try {
            ProductResponseDTO productResponse = productService.getProductById(productId);
            return ResponseEntity.ok(productResponse);
        } catch (ResponseStatusException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Erro de Status",
                    "Não conseguimos encontrar o produto com o ID fornecido. Verifique o ID e tente novamente."
            );
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Erro Interno",
                    "Ocorreu um erro inesperado. Tente novamente mais tarde."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "Find all products", description = "Finds all products",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/for-sale")
    @Operation(summary = "Find all products for sale", description = "Finds all products available for sale",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductSaleDTO.class)))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<ProductSaleDTO>> getAllProductsForSale() {
        List<ProductSaleDTO> productList = productService.getAllProductsForSale();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Find products with low stock", description = "Finds products with low stock",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content)
            }
    )
    public ResponseEntity<List<ProductResponseDTO>> getProductsWithLowStock() {
        List<ProductResponseDTO> lowStockProducts = productService.getProductsWithLowStock();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/check-stock")
    @Operation(summary = "Check stock and send alert", description = "Checks stock and sends an alert if low",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "text/plain")),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<String> checkStock() {
        List<Product> lowStockProducts = productService.getRawProductsWithLowStock();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.ok("Nenhum produto com estoque baixo.");
        } else {
            emailService.sendLowStockAlert(lowStockProducts);
            return ResponseEntity.ok("Alerta de estoque baixo enviado por e-mail.");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponse = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @PostMapping("/create")
    @Operation(summary = "Create multiple products", description = "Creates multiple products",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<List<ProductResponseDTO>> createProducts(@RequestBody List<ProductRequestDTO> productRequestDTOs) {
        List<ProductResponseDTO> productResponses = productService.createProducts(productRequestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponses);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update a product", description = "Updates an existing product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("productId") Long productId, @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "Partially update a product", description = "Partially updates an existing product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<ProductResponseDTO> partialUpdateProduct(@PathVariable("productId") Long productId, @RequestBody Map<String, Object> updates) {
        ProductResponseDTO updatedProduct = productService.partialUpdateProduct(productId, updates);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }
}
package com.salesphere.salesphere.controllers.product;

import com.salesphere.salesphere.exceptions.ErrorResponse;
import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.models.dto.product.request.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.product.response.ProductResponseDTO;
import com.salesphere.salesphere.models.dto.payment.sale.ProductSaleDTO;
import com.salesphere.salesphere.services.product.ProductService;
import com.salesphere.salesphere.services.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping(value = "/products-test")
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
    public ResponseEntity<EntityModel<ProductResponseDTO>> getProductById(@PathVariable("id") Long productId) {
        try {
            ProductResponseDTO productResponse = productService.getProductById(productId);
            EntityModel<ProductResponseDTO> resource = EntityModel.of(productResponse);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(productId)).withSelfRel();
            resource.add(selfLink);
            return ResponseEntity.ok(resource);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Não conseguimos encontrar o produto com o ID fornecido. Verifique o ID e tente novamente.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.");
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
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDTO>>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponseDTO> productPage = productService.getAllProducts(page, size);
        List<EntityModel<ProductResponseDTO>> productResources = productPage.getContent().stream()
                .map(product -> {
                    EntityModel<ProductResponseDTO> resource = EntityModel.of(product);
                    Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getProductById(product.id())).withSelfRel();
                    resource.add(selfLink);
                    return resource;
                })
                .collect(Collectors.toList());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).getAllProducts(page, size)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(productResources, selfLink));
    }

    @GetMapping("/search")
    @Operation(summary = "Find products by SKU", description = "Finds products by their SKU code",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<ProductResponseDTO>> searchProductsBySku(@RequestParam("sku") String sku) {
        try {
            List<ProductResponseDTO> products = productService.getProductsBySku(sku);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Erro Interno",
                    "Ocorreu um erro inesperado ao buscar produtos por código SKU. Tente novamente mais tarde."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((List<ProductResponseDTO>) errorResponse);
        }
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
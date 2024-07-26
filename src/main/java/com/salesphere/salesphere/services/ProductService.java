package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository repository, ProductMapper productMapper) {
        this.repository = repository;
        this.productMapper = productMapper;
    }

    public List<ProductResponseDTO> getProducts() {
        List<Product> products = repository.findAll();
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toProduct(productRequestDTO);
        Product savedProduct = repository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    public List<Product> getProductsWithLowStock() {
        List<Product> allProducts = repository.findAll();
        return allProducts.stream()
                .filter(product -> product.getStockQuantity() < product.getMinimumQuantity())
                .collect(Collectors.toList());
    }

    public void checkStock() {
        List<Product> productsWithStockLow = getProductsWithLowStock();
        for (Product product : productsWithStockLow) {
            sendLowStockAlert(product);
        }
    }

    private void sendLowStockAlert(Product product) {
        //TODO: Implementação da lógica de envio de alerta, por exemplo, email ou notificação
    }
}
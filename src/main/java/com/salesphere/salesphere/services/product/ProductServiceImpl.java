package com.salesphere.salesphere.services.product;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductServiceInterface {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDTO> findProductsBySku(String sku) {
        List<Product> products = productRepository.findByCodeSkuContainingIgnoreCase(sku);
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}

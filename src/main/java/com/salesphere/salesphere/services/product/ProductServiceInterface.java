package com.salesphere.salesphere.services.product;

import com.salesphere.salesphere.models.dto.product.response.ProductResponseDTO;

import java.util.List;

public interface ProductServiceInterface {

    List<ProductResponseDTO> findProductsBySku(String sku);
}

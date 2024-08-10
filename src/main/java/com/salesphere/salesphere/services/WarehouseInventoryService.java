package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.Warehouse;
import com.salesphere.salesphere.models.WarehouseProduct;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.repositories.WarehouseProductRepository;
import com.salesphere.salesphere.repositories.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseInventoryService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final WarehouseProductRepository warehouseProductRepository;

    public WarehouseInventoryService(WarehouseRepository warehouseRepository, ProductRepository productRepository, WarehouseProductRepository warehouseProductRepository) {
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.warehouseProductRepository = warehouseProductRepository;
    }

    @Transactional
    public void transferProduct(Long productId, Long fromWarehouseId, Long toWarehouseId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        Warehouse fromWarehouse = warehouseRepository.findById(fromWarehouseId).orElseThrow(() -> new RuntimeException("Armazém de origem não encontrado"));
        Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId).orElseThrow(() -> new RuntimeException("Armazém de destino não encontrado"));

        WarehouseProduct fromWarehouseProduct = warehouseProductRepository.findByWarehouseAndProduct(fromWarehouseId, productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no armazém de origem"));

        if (fromWarehouseProduct.getQuantity() < quantity) {
            throw new RuntimeException("Quantidade insuficiente no armazém de origem");
        }

        fromWarehouseProduct.setQuantity(fromWarehouseProduct.getQuantity() - quantity);
        warehouseProductRepository.save(fromWarehouseProduct);

        WarehouseProduct toWarehouseProduct = warehouseProductRepository.findByWarehouseAndProduct(toWarehouseId, productId)
                .orElse(new WarehouseProduct(toWarehouse, product, 0));

        toWarehouseProduct.setQuantity(toWarehouseProduct.getQuantity() + quantity);
        warehouseProductRepository.save(toWarehouseProduct);
    }
}
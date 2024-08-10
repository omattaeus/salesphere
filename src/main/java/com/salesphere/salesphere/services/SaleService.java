package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.Sale;
import com.salesphere.salesphere.models.SaleItem;
import com.salesphere.salesphere.models.dto.ProductSaleDTO;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.repositories.SaleRepository;
import com.salesphere.salesphere.repositories.SaleItemRepository;
import com.salesphere.salesphere.services.discount.DiscountPolicy;
import com.salesphere.salesphere.services.discount.DiscountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final DiscountService discountService;

    public SaleService(SaleRepository saleRepository, SaleItemRepository saleItemRepository,
                       ProductRepository productRepository, DiscountService discountService) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.discountService = discountService;
    }

    @Transactional
    public Sale createSale(List<ProductSaleDTO> productSaleDTOs, DiscountPolicy discountPolicy) {
        if (productSaleDTOs == null) {
            throw new IllegalArgumentException("A lista de itens de venda não pode ser null");
        }

        Sale sale = new Sale();
        sale.setSaleDate(LocalDateTime.now());

        double totalAmount = 0;
        for (ProductSaleDTO dto : productSaleDTOs) {
            Product product = productRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setQuantity(dto.getQuantity());
            saleItem.setPricePerUnit(dto.getSalePrice());

            double discountedPrice = discountService.applyDiscount(product, discountPolicy);
            saleItem.setPricePerUnit(discountedPrice);

            totalAmount += saleItem.getTotalPrice();
            sale.addSaleItem(saleItem);

            updateProductStock(product, dto.getQuantity());
        }

        if (discountPolicy != null) {
            totalAmount = discountPolicy.apply(totalAmount);
        }

        sale.setTotalAmount(totalAmount);
        return saleRepository.save(sale);
    }

    private void updateProductStock(Product product, Long quantitySold) {
        Long newStockQuantity = product.getStockQuantity() - quantitySold;
        if (newStockQuantity < 0) {
            throw new RuntimeException("Quantidade em estoque insuficiente para o produto: " + product.getProductName());
        }
        product.setStockQuantity(newStockQuantity);
        productRepository.save(product);
    }

    public Sale getSaleById(Long saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }
}
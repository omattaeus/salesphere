package com.salesphere.salesphere.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;

@Entity
@Table(name = "tb_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "O nome do produto não pode estar vazio!")
    @Column(name = "product_name", unique = true)
    private String productName;

    @NotNull(message = "Descrição não pode estar vazia!")
    @Column(name = "description", length = 100)
    private String description;

    @NotNull(message = "A marca não pode estar vazia!")
    @Column(name = "brand")
    private String brand;

    @NotNull(message = "O preço de compra não pode estar vazio!")
    @Column(name = "purchase_price")
    private Double purchasePrice;

    @NotNull(message = "O preço de venda não pode estar vazio!")
    @Column(name = "sale_price")
    private Double salePrice;

    @NotNull(message = "A quantidade em estoque não pode estar vazia!")
    @Column(name = "stock_quantity")
    private Long stockQuantity;

    @NotNull(message = "A quantidade mínima em estoque não pode estar vazia!")
    @Column(name = "minimum_quantity")
    private Long minimumQuantity;

    @NotNull(message = "Código SKU não pode estar vazio!")
    @Column(name = "code_sku", unique = true)
    private String codeSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "O status de disponibilidade não pode estar vazio!")
    @Enumerated(EnumType.STRING)
    @Column(name = "availability")
    private AvailabilityEnum availability;

    // Construtor com todos os campos necessários
    public Product(Long id, String productName, String description,
                   String brand, Category category, Double purchasePrice,
                   Double salePrice, Long stockQuantity, Long minimumQuantity,
                   String codeSku, AvailabilityEnum availability) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.minimumQuantity = minimumQuantity;
        this.codeSku = codeSku;
        this.availability = availability;
    }
}
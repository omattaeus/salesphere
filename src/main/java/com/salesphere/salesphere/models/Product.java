package com.salesphere.salesphere.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "O nome do produto não pode estar vazio!")
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;

    @NotNull(message = "Descrição não pode estar vazia!")
    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @NotNull(message = "A marca não pode estar vazia!")
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotNull(message = "O preço de compra não pode estar vazio!")
    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;

    @NotNull(message = "O preço de venda não pode estar vazio!")
    @Column(name = "sale_price", nullable = false)
    private Double salePrice;

    @NotNull(message = "A quantidade em estoque não pode estar vazia!")
    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @NotNull(message = "A quantidade mínima em estoque não pode estar vazia!")
    @Column(name = "minimum_quantity", nullable = false)
    private Long minimumQuantity;

    @NotNull(message = "Código SKU não pode estar vazio!")
    @Column(name = "code_sku", unique = true, nullable = false)
    @JsonProperty(value = "code_sku")
    private String codeSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

    public Product(Long id, String productName, String description, String brand,
                   Double purchasePrice, Double salePrice, Long stockQuantity,
                   Long minimumQuantity, String codeSku, Category category,
                   Availability availability) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.brand = brand;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.minimumQuantity = minimumQuantity;
        this.codeSku = codeSku;
        this.category = category;
        this.availability = availability;
    }
}
package com.salesphere.salesphere.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O nome do produto não pode estar vazio!")
    @Column(name = "product_name", unique = true)
    private String productName;

    @NotNull(message = "Descrição não pode estar vazia!")
    @Column(length = 100)
    private String description;

    @NotNull(message = "A marca não pode estar vazia!")
    private String brand;

    @NotNull(message = "O preço de compra não pode estar vazio!")
    private Double purchasePrice;

    @NotNull(message = "O preço de venda não pode estar vazio!")
    private Double salePrice;

    @NotNull(message = "A quantidade em estoque não pode estar vazia!")
    private Long stockQuantity;

    @NotNull(message = "A quantidade mínima em estoque não pode estar vazia!")
    private Long minimumQuantity;

    @NotNull(message = "Código SKU não pode estar vazio!")
    @Column(name = "code_sku", unique = true)
    private String codeSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Construtor com todos os campos necessários
    public Product(Long id, String productName, String description,
                   String brand, Category category, Double purchasePrice,
                   Double salePrice, Long stockQuantity, Long minimumQuantity,
                   String codeSku) {
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
    }
}
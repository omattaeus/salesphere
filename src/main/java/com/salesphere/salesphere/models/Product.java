package com.salesphere.salesphere.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "product_name")
    private String productName;
    @Column(name = "brand")
    private String brand;
    @NotNull
    @Column(name = "category")
    private String category;
    @NotNull
    @Column(name = "purchase_price")
    private String purchasePrice;
    @NotNull
    @Column(name = "sale_price")
    private String salePrice;
    @NotNull
    @Column(name = "stock_quantity")
    private String stockQuantity;
    @NotNull
    @Column(name = "code_sku")
    private String codeSku;
}
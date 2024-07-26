package com.salesphere.salesphere.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull(message = "O nome do produto não pode estar vazio!")
    private String productName;

    @NotNull(message = "A marca não pode estar vazia!")
    private String brand;

    @NotNull(message = "A categoria não pode estar vazia!")
    private String category;

    @NotNull(message = "O preço de compra não pode estar vazio!")
    private Double purchasePrice;

    @NotNull(message = "O preço de venda não pode estar vazio!")
    private Double salePrice;

    @NotNull(message = "A quantidade em estoque não pode estar vazia!")
    private Long stockQuantity;

    @NotNull(message = "A quantidade mínima em estoque não pode estar vazia!")
    private Long minimumQuantity;

    @NotNull(message = "Código SKU não pode estar vazio!")
    private String codeSku;
}
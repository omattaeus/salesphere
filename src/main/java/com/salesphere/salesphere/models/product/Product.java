package com.salesphere.salesphere.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.inventory.InventoryMovement;
import com.salesphere.salesphere.models.product.warehouse.WarehouseProduct;
import com.salesphere.salesphere.models.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "inventory_item_id")
    private Long inventoryItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    @JsonBackReference
    private Availability availability;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_enum")
    private StatusEnum status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<InventoryMovement> inventoryMovements = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<WarehouseProduct> warehouseProducts = new HashSet<>();
}
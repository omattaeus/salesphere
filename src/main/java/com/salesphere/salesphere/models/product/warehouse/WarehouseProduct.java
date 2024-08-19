package com.salesphere.salesphere.models.product.warehouse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.salesphere.salesphere.models.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_warehouse_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;


    @Column(name = "quantity")
    private int quantity;

    public WarehouseProduct(Warehouse warehouse, Product product, int quantity) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }
}
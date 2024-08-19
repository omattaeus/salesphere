package com.salesphere.salesphere.models.sale;

import com.salesphere.salesphere.models.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_sale_item")
@Getter
@Setter
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;

    public double getTotalPrice() {
        return quantity * pricePerUnit;
    }
}
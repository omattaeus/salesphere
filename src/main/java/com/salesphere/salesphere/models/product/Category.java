package com.salesphere.salesphere.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.models.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", unique = true, length = 20, nullable = false)
    private CategoryEnum categoryEnum;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }

    @Override
    public String toString() {
        return categoryEnum != null ? categoryEnum.toString() : "N/A";
    }
}
package com.salesphere.salesphere.services.report;

import com.salesphere.salesphere.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReport {

    private List<Product> products;
}

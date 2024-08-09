package com.salesphere.salesphere.services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockUpdatePayload {
    private Long inventoryItemId;
    private Long quantity;
}
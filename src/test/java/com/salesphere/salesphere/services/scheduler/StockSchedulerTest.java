package com.salesphere.salesphere.services.scheduler;

import com.salesphere.salesphere.services.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class StockSchedulerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockScheduler stockScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should call checkStock method periodically")
    public void testCheckStockPeriodically() {

        stockScheduler.checkStockPeriodically();

        verify(productService, times(1)).checkStock();
    }
}

package com.salesphere.salesphere.services.converter;

import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductUpdaterTest {

    @Mock
    private FieldValueConverter converter;

    @InjectMocks
    private ProductUpdater productUpdater;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        // Provide a list of mock converters
        productUpdater = new ProductUpdater(Collections.singletonList(converter));
    }

    @Test
    @DisplayName("Should correctly map and update field values in Product")
    void shouldApplyUpdatesToProduct() throws Exception {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code_sku", "newCode");
        updates.put("availability", true); // Use Boolean value

        // When
        productUpdater.applyUpdates(product, updates);

        // Then
        Field codeSkuField = ReflectionUtils.findField(Product.class, "codeSku");
        Field availabilityField = ReflectionUtils.findField(Product.class, "availability");
        assertNotNull(codeSkuField);
        assertNotNull(availabilityField);
        codeSkuField.setAccessible(true);
        availabilityField.setAccessible(true);
        assertEquals("newCode", codeSkuField.get(product));
        Availability availability = (Availability) availabilityField.get(product);
        assertNotNull(availability);
        assertEquals(AvailabilityEnum.AVAILABLE, availability.getAvailability());
    }

    @Test
    @DisplayName("Should throw exception for unknown field")
    void shouldThrowExceptionForUnknownField() {
        // Given
        Map<String, Object> updates = Collections.singletonMap("unknown_field", "value");

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productUpdater.applyUpdates(product, updates)
        );
        assertEquals("Campo desconhecido: unknown_field", exception.getMessage());
    }

    @Test
    @DisplayName("Should use converters to convert field values")
    void shouldUseConvertersToConvertValues() {
        // Given
        Map<String, Object> updates = new HashMap<>();
        updates.put("code_sku", "convertedCode");
        when(converter.supports(String.class)).thenReturn(true);
        when(converter.convert("convertedCode", String.class)).thenReturn("convertedValue");

        // When
        productUpdater.applyUpdates(product, updates);

        // Then
        verify(converter).convert("convertedCode", String.class);
        Field codeSkuField = ReflectionUtils.findField(Product.class, "codeSku");
        assertNotNull(codeSkuField);
        codeSkuField.setAccessible(true);
        assertEquals("convertedValue", ReflectionUtils.getField(codeSkuField, product));
    }
}
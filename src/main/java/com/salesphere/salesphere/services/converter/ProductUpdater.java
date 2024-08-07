package com.salesphere.salesphere.services.converter;

import com.salesphere.salesphere.models.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;

@Component
public class ProductUpdater {

    private final List<FieldValueConverter> converters;
    private static final Map<String, String> FIELD_NAME_MAPPING = Map.of(
            "code_sku", "codeSKU",
            "availability", "availability"
    );

    public ProductUpdater(List<FieldValueConverter> converters) {
        this.converters = converters;
    }

    public void applyUpdates(Product product, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            String fieldName = FIELD_NAME_MAPPING.getOrDefault(key, key);
            Field field = ReflectionUtils.findField(Product.class, fieldName);
            if (field != null) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object convertedValue = convertValue(value, fieldType);
                ReflectionUtils.setField(field, product, convertedValue);
            } else {
                throw new IllegalArgumentException("Campo desconhecido: " + key);
            }
        });
    }

    private Object convertValue(Object value, Class<?> targetType) {
        for (FieldValueConverter converter : converters) {
            if (converter.supports(targetType)) {
                return converter.convert(value, targetType);
            }
        }
        return value;
    }
}
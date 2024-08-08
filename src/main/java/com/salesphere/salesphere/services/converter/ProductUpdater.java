package com.salesphere.salesphere.services.converter;

import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Component
public class ProductUpdater {

    private final List<FieldValueConverter> converters;
    private static final Map<String, String> FIELD_NAME_MAPPING = Map.of(
            "code_sku", "codeSku",
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
        // Check if a suitable converter is available
        for (FieldValueConverter converter : converters) {
            if (converter.supports(targetType)) {
                return converter.convert(value, targetType);
            }
        }

        // Handle specific conversion for Availability
        if (targetType.equals(Availability.class)) {
            if (value instanceof Boolean) {
                // Convert Boolean to AvailabilityEnum
                AvailabilityEnum availabilityEnum = (Boolean) value ? AvailabilityEnum.AVAILABLE : AvailabilityEnum.OUT_OF_STOCK;
                return new Availability(availabilityEnum);
            } else if (value instanceof String) {
                // Convert String to AvailabilityEnum
                try {
                    AvailabilityEnum availabilityEnum = AvailabilityEnum.valueOf((String) value);
                    return new Availability(availabilityEnum);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid value for Availability: " + value);
                }
            }
        }

        // Handle other custom types or default conversion
        if (targetType.isEnum() && value instanceof String) {
            // Convert String to Enum
            try {
                return Enum.valueOf((Class<Enum>) targetType, (String) value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid value for enum type: " + value);
            }
        }

        // Handle default conversion (e.g., if the type is a number or String)
        return value;
    }
}
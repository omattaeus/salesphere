package com.salesphere.salesphere.services.converter;

import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.product.Product;
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
        if (product == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo.");
        }

        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("O mapa de atualizações não pode ser nulo ou vazio.");
        }

        updates.forEach((key, value) -> {
            String fieldName = FIELD_NAME_MAPPING.getOrDefault(key, key);
            Field field = ReflectionUtils.findField(Product.class, fieldName);

            if (field == null) {
                throw new IllegalArgumentException("Campo desconhecido: " + key);
            }

            try {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object convertedValue = convertValue(value, fieldType);
                ReflectionUtils.setField(field, product, convertedValue);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao atualizar o campo '" + fieldName + "' no produto.", e);
            }
        });
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        for (FieldValueConverter converter : converters) {
            if (converter.supports(targetType)) {
                return converter.convert(value, targetType);
            }
        }

        if (targetType.equals(Availability.class)) {
            return convertToAvailability(value);
        }

        if (targetType.isEnum() && value instanceof String) {
            return convertToEnum(value, targetType);
        }

        throw new IllegalArgumentException("Conversão não suportada para o tipo: " + targetType.getName() + " com valor: " + value);
    }

    private Availability convertToAvailability(Object value) {
        if (value instanceof Boolean) {
            AvailabilityEnum availabilityEnum = (Boolean) value ? AvailabilityEnum.AVAILABLE : AvailabilityEnum.OUT_OF_STOCK;
            return new Availability(availabilityEnum);
        } else if (value instanceof String) {
            try {
                AvailabilityEnum availabilityEnum = AvailabilityEnum.valueOf((String) value);
                return new Availability(availabilityEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor inválido para Availability: " + value, e);
            }
        }
        throw new IllegalArgumentException("Valor inválido para Availability: " + value);
    }

    private Object convertToEnum(Object value, Class<?> targetType) {
        try {
            return Enum.valueOf((Class<Enum>) targetType, (String) value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para o tipo enum " + targetType.getSimpleName() + ": " + value, e);
        }
    }
}
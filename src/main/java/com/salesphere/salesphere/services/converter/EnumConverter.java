package com.salesphere.salesphere.services.converter;

public class EnumConverter implements FieldValueConverter {
    @Override
    public boolean supports(Class<?> fieldType) {
        return fieldType.isEnum();
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value instanceof String) {
            try {
                return Enum.valueOf((Class<? extends Enum>) targetType, ((String) value).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor inválido para enum: " + value, e);
            }
        }
        return value;
    }
}
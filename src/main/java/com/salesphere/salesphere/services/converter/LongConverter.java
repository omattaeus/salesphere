package com.salesphere.salesphere.services.converter;

public class LongConverter implements FieldValueConverter {
    @Override
    public boolean supports(Class<?> fieldType) {
        return Long.class.equals(fieldType);
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        return value;
    }
}
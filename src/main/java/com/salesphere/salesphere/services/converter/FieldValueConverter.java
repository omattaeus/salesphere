package com.salesphere.salesphere.services.converter;


public interface FieldValueConverter {
    boolean supports(Class<?> fieldType);
    Object convert(Object value, Class<?> targetType);
}


package com.salesphere.salesphere.services.converter;

public class EnumConverter implements FieldValueConverter {

    @Override
    public boolean supports(Class<?> fieldType) {
        return fieldType.isEnum();
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value == null) {
            throw new IllegalArgumentException("O valor para conversão não pode ser nulo.");
        }

        if (value instanceof String) {
            try {
                return Enum.valueOf((Class<? extends Enum>) targetType, ((String) value).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor inválido para enum do tipo " + targetType.getSimpleName() + ": " + value, e);
            }
        } else {
            throw new IllegalArgumentException("Tipo de valor incompatível para enum. Esperado uma String, mas recebeu: " + value.getClass().getSimpleName());
        }
    }
}
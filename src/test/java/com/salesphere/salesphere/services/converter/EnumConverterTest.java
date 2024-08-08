package com.salesphere.salesphere.services.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

enum TestEnum {
    VALUE1, VALUE2;
}

class EnumConverterTest {

    private final EnumConverter converter = new EnumConverter();

    @Test
    @DisplayName("Should support Enum types")
    void shouldSupportEnumTypes() {
        // Given
        Class<?> enumType = TestEnum.class;

        // When
        boolean supportsEnum = converter.supports(enumType);

        // Then
        assertTrue(supportsEnum, "Should support Enum types");
    }

    @Test
    @DisplayName("Should not support non-Enum types")
    void shouldNotSupportNonEnumTypes() {
        // Given
        Class<?> stringType = String.class;
        Class<?> integerType = Integer.class;

        // When
        boolean supportsString = converter.supports(stringType);
        boolean supportsInteger = converter.supports(integerType);

        // Then
        assertFalse(supportsString, "Should not support non-Enum types");
        assertFalse(supportsInteger, "Should not support non-Enum types");
    }

    @Test
    @DisplayName("Should convert valid String to Enum")
    void shouldConvertValidStringToEnum() {
        // Given
        String validValue = "VALUE1";

        // When
        TestEnum result = (TestEnum) converter.convert(validValue, TestEnum.class);

        // Then
        assertEquals(TestEnum.VALUE1, result, "Should convert valid String to the corresponding Enum");
    }

    @Test
    @DisplayName("Should throw exception for invalid String")
    void shouldThrowExceptionForInvalidString() {
        // Given
        String invalidValue = "INVALID";

        // When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                converter.convert(invalidValue, TestEnum.class)
        );

        // Then
        assertEquals("Valor inválido para enum: " + invalidValue, thrown.getMessage());
    }

    @Test
    @DisplayName("Should return the original value for non-String types")
    void shouldReturnOriginalValueForNonStringTypes() {
        // Given
        Integer nonStringValue = 123;

        // When
        Object result = converter.convert(nonStringValue, TestEnum.class);

        // Then
        assertEquals(nonStringValue, result, "Should return the original value for non-String types");
    }
}
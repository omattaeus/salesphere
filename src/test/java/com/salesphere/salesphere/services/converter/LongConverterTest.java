package com.salesphere.salesphere.services.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongConverterTest {

    private final LongConverter converter = new LongConverter();

    @Test
    @DisplayName("Should support Long type")
    void shouldSupportLongType() {
        // Given
        Class<?> longClass = Long.class;

        // When
        boolean supportsLong = converter.supports(longClass);

        // Then
        assertTrue(supportsLong, "Converter should support Long type");
    }

    @Test
    @DisplayName("Should not support types other than Long")
    void shouldNotSupportNonLongTypes() {
        // Given
        Class<?> integerClass = Integer.class;
        Class<?> stringClass = String.class;
        Class<?> doubleClass = Double.class;

        // When
        boolean supportsInteger = converter.supports(integerClass);
        boolean supportsString = converter.supports(stringClass);
        boolean supportsDouble = converter.supports(doubleClass);

        // Then
        assertFalse(supportsInteger, "Converter should not support Integer type");
        assertFalse(supportsString, "Converter should not support String type");
        assertFalse(supportsDouble, "Converter should not support Double type");
    }

    @Test
    @DisplayName("Should convert Integer to Long")
    void shouldConvertIntegerToLong() {
        // Given
        Integer integerValue = 42;

        // When
        Object result = converter.convert(integerValue, Long.class);

        // Then
        assertEquals(42L, result, "Converter should convert Integer to Long");
    }

    @Test
    @DisplayName("Should return the original value if not an Integer")
    void shouldReturnOriginalValueForNonInteger() {
        // Given
        String stringValue = "Test";
        Double doubleValue = 3.14;

        // When
        Object resultFromString = converter.convert(stringValue, Long.class);
        Object resultFromDouble = converter.convert(doubleValue, Long.class);

        // Then
        assertEquals(stringValue, resultFromString, "Converter should return original String value");
        assertEquals(doubleValue, resultFromDouble, "Converter should return original Double value");
    }

    @Test
    @DisplayName("Should not alter Long values")
    void shouldNotAlterLongValues() {
        // Given
        Long longValue = 100L;

        // When
        Object result = converter.convert(longValue, Long.class);

        // Then
        assertEquals(longValue, result, "Converter should not alter Long values");
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValues() {
        // Given
        Object nullValue = null;

        // When
        Object result = converter.convert(nullValue, Long.class);

        // Then
        assertNull(result, "Converter should handle null values gracefully");
    }
}
package com.guru.researchplatform.marketdatacollector.api.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationResult Tests")
class ValidationResultTest {

    @Test
    @DisplayName("should create successful validation result")
    void shouldCreateSuccessfulValidationResult() {
        ValidationResult result = ValidationResult.success();

        assertTrue(result.valid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("should create failed validation with single error")
    void shouldCreateFailedValidationWithSingleError() {
        ValidationResult result = ValidationResult.failure("Invalid asset");

        assertFalse(result.valid());
        assertEquals(1, result.errors().size());
        assertEquals("Invalid asset", result.errors().get(0));
    }

    @Test
    @DisplayName("should create failed validation with multiple errors")
    void shouldCreateFailedValidationWithMultipleErrors() {
        List<String> errors = List.of("Invalid asset", "Time range too large", "Limit must be positive");
        ValidationResult result = ValidationResult.failures(errors);

        assertFalse(result.valid());
        assertEquals(3, result.errors().size());
    }

    @Test
    @DisplayName("should throw when failure() called with null error")
    void shouldThrowWhenNullErrorInFailure() {
        assertThrows(NullPointerException.class, () -> ValidationResult.failure(null));
    }

    @Test
    @DisplayName("should throw when failures() called with null list")
    void shouldThrowWhenNullListInFailures() {
        assertThrows(NullPointerException.class, () -> ValidationResult.failures(null));
    }

    @Test
    @DisplayName("should throw when failures() called with empty list")
    void shouldThrowWhenEmptyListInFailures() {
        assertThrows(IllegalArgumentException.class, () -> ValidationResult.failures(List.of()));
    }

    @Test
    @DisplayName("should return immutable errors list")
    void shouldReturnImmutableErrorsList() {
        ValidationResult result = ValidationResult.failure("Error 1");
        List<String> errors = result.errors();

        assertThrows(UnsupportedOperationException.class, () -> errors.add("Error 2"));
    }

    @Test
    @DisplayName("should distinguish valid from invalid results")
    void shouldDistinguishValidFromInvalid() {
        ValidationResult valid = ValidationResult.success();
        ValidationResult invalid = ValidationResult.failure("Error");

        assertTrue(valid.valid());
        assertFalse(invalid.valid());
        assertNotEquals(valid, invalid);
    }
}

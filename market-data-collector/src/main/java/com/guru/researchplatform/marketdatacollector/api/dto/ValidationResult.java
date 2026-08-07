package com.guru.researchplatform.marketdatacollector.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of validating a download request.
 * 
 * Immutable record capturing validation status and any error messages
 * that occurred during request validation.
 */
public record ValidationResult(
    boolean valid,
    List<String> errors
) {
    /**
     * Compact constructor for validation.
     * 
     * Ensures the errors list is not null, creating an empty list if needed.
     */
    public ValidationResult {
        Objects.requireNonNull(errors, "errors list cannot be null");
        // Make defensive copy to preserve immutability
        errors = new ArrayList<>(errors);
    }

    /**
     * Creates a successful validation result with no errors.
     * 
     * @return a valid ValidationResult
     */
    public static ValidationResult success() {
        return new ValidationResult(true, List.of());
    }

    /**
     * Creates a failed validation result with the specified error message.
     * 
     * @param error the validation error message
     * @return a failed ValidationResult with the error
     * @throws NullPointerException if error is null
     */
    public static ValidationResult failure(String error) {
        Objects.requireNonNull(error, "error message cannot be null");
        return new ValidationResult(false, List.of(error));
    }

    /**
     * Creates a failed validation result with multiple error messages.
     * 
     * @param errors the list of validation errors
     * @return a failed ValidationResult with the errors
     * @throws NullPointerException if errors is null
     * @throws IllegalArgumentException if errors is empty
     */
    public static ValidationResult failures(List<String> errors) {
        Objects.requireNonNull(errors, "errors list cannot be null");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors list cannot be empty");
        }
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    /**
     * Returns an unmodifiable copy of the errors list.
     * 
     * @return an immutable view of the errors
     */
    @Override
    public List<String> errors() {
        return List.copyOf(errors);
    }
}

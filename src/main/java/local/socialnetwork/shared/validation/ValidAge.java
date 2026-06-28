package local.socialnetwork.shared.validation;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Validates that a {@link java.time.LocalDate} birth date represents an age
 * between {@value #MIN_AGE} and {@value #MAX_AGE} years (inclusive).
 *
 * <p>Null values are considered valid — pair with {@code @NotNull} when a value
 * is required.
 */
@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {

    int MIN_AGE = 13;
    int MAX_AGE = 120;

    String message() default "Age must be between " + MIN_AGE + " and " + MAX_AGE + " years";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

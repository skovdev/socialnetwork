package local.socialnetwork.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Period;
import java.time.LocalDate;

/**
 * Checks that a birth date corresponds to an age within the range defined by
 * {@link ValidAge#MIN_AGE} and {@link ValidAge#MAX_AGE}.
 */
public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(ValidAge annotation) {
        this.minAge = annotation.MIN_AGE;
        this.maxAge = annotation.MAX_AGE;
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age >= minAge && age <= maxAge;
    }
}

package mate.academy.bookingservice.validation;

import java.util.EnumSet;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<Status, String> {
    private EnumSet enumValues;

    @Override
    public void initialize(Status constraintAnnotation) {
        Class enumClass = constraintAnnotation.enumClass();
        this.enumValues = EnumSet.allOf(enumClass);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (Object enumValue : enumValues) {
            if (enumValue.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}

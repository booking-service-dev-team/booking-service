package mate.academy.bookingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.EnumSet;

public class AccommodationTypeValidator implements ConstraintValidator<AccommodationType, String> {
    private EnumSet enumValues;

    @Override
    public void initialize(AccommodationType constraintAnnotation) {
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

package mate.academy.bookingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        Object firstFieldValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object secondFieldValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
        return firstFieldValue != null && firstFieldValue.equals(secondFieldValue);
    }
}


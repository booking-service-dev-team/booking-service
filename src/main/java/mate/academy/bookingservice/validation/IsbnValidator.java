package mate.academy.bookingservice.validation;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    /*
    ISBN-13: Consists of 13 digits and usually includes a country number,
    a publisher prefix, a book type number, a specific book number, and a check digit.
    ISBN-13 is a more modern format and is recommended for new editions of books.
    Example
    ISBN-13: 978-0-345-32426-8.
     */

    public static final String PATTERN_OF_ISBN =
            "^(?:ISBN(?:-13)?:?\\s?)?(?=[0-9]{13}$|(?=(?:[0-9]+[-\\s]){4})"
                    + "[-\\s0-9]*$|97[89][-\\s]?[0-9]{10}$|(?=(?:[0-9]+[-\\s]){3})[-\\s0-9]"
                    + "*$)(?:97[89][-\\s]?)([0-9]{1,5})[-\\s]?([0-9]+)[-\\s]?([0-9]+)[-\\s]?"
                    + "([0-9])$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return isbn != null && Pattern.compile(PATTERN_OF_ISBN).matcher(isbn).matches();
    }
}

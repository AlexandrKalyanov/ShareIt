package ru.practicum.shareit.customValidation;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingDtoRequest> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoRequest value, ConstraintValidatorContext context) {
        if (value.getStart() == null || value.getEnd() == null) {
            return false;
        }
        return !value.getStart().isAfter(value.getEnd()) &&
                !value.getEnd().isBefore(value.getStart()) &&
                !value.getStart().isBefore(LocalDateTime.now()) &&
                !value.getStart().equals(value.getEnd());
    }
}

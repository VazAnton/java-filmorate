package ru.yandex.practicum.filmorate.customConstraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MyLocalDateValidator implements ConstraintValidator<IsAfter, LocalDate> {

    private final LocalDate firstFilmRelease = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate != null && releaseDate.isAfter(firstFilmRelease);
    }
}

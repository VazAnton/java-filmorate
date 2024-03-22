package ru.yandex.practicum.filmorate.customConstraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MyLocalDateValidator.class)
public @interface IsAfter {
    String message() default "Дата релиза должна быть указана и не может быть раньше даты релиза первого " +
            "фильма!";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

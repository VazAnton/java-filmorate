package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.customConstraints.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@Builder
public class Film {
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @Length(max = 200)
    private String description;
    @IsAfter
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private List<Genre> genres;
    private Rating mpa;
    private List<Director> directors;
}

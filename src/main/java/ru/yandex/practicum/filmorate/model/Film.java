package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Genre> genres;
    private Rating mpa;
}

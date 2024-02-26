package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class Rating {
    private Integer id;
    private String name;
}

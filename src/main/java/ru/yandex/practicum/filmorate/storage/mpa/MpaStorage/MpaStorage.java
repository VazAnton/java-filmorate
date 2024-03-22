package ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface MpaStorage {

    Rating getRating(int id);

    List<Rating> getRatings();
}

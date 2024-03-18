package ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director getDirector(int id);

    List<Director> getDirectors();

    boolean deleteDirector(int id);
}

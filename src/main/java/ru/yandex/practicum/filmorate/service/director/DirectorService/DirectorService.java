package ru.yandex.practicum.filmorate.service.director.DirectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage.DirectorStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        log.info("Информация о режиссере успешно создана!");
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Информация о режиссере успешно обновлена!");
        return directorStorage.updateDirector(director);
    }

    public Director getDirector(int id) {
        log.info("Информация о выбранном режиссере успешно получена!");
        return directorStorage.getDirector(id);
    }

    public List<Director> getDirectors() {
        log.info("Поучена информация обо всех режиссёрах!");
        return directorStorage.getDirectors();
    }

    public boolean deleteDirector(int id) {
        log.info("Информация о выбранном режиссере успешно удалена!");
        return directorStorage.deleteDirector(id);
    }
}

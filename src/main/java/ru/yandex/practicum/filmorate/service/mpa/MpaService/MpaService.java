package ru.yandex.practicum.filmorate.service.mpa.MpaService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage.MpaStorage;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    public Rating getRating(int id) {
        log.info("Информация о выбранном рейтинге успешно получена!");
        return mpaStorage.getRating(id);
    }

    public List<Rating> getRatings() {
        log.info("Информация обо всех рейтингах успешно получена!");
        return mpaStorage.getRatings();
    }
}

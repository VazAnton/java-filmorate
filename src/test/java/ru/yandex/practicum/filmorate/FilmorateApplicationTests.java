package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;
    public InMemoryFilmStorage inMemoryFilmStorage;
    public InMemoryUserStorage inMemoryUserStorage;
    public UserService userService;
    public FilmService filmService;
    @BeforeEach
    public void setUp() {
        filmController = new FilmController(inMemoryFilmStorage, filmService);
        userController = new UserController(inMemoryUserStorage, userService);
    }

    @Test
    public void checkAddFilmIfValidationIsFine() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        assertEquals(0, filmController.getFilms().size());

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmNameIsEmpty() {
        Film film1 = new Film(1, "", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );

        assertEquals("Название фильма не может быть пустым!", validationException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void addShouldNotThrowValidationExceptionIfFilmDescriptionIs200Characters() {
        Film film1 = new Film(1, "Маска", "Фильм \"Маска\" -  это захватывающая комедия, где " +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы. " +
                "Фильм смешной и непременно заставит вас улыбнуться.", LocalDate.of(2003, 3,
                26), 126);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmDescriptionIsMoreThen200Characters() {
        Film film1 = new Film(1, "Маска", "Фильм Маска - это захватывающая комедийная " +
                "приключенческая история о скромном банковском служащем по имени Стэнли Ипкисс. Однажды он находит " +
                "древнюю маску, которая превращает его в эксцентричного супергероя. Стэнли начинает использовать свои " +
                "новые суперспособности для борьбы с преступностью и покорения сердца красавицы. Но с каждым днем он " +
                "понимает, что маска имеет свою темную сторону. Фильм полон юмора, экшена и неожиданных поворотов " +
                "сюжета. Не пропустите эту захватывающую историю о супергерое-неудачнике!",
                LocalDate.of(2003, 3,
                26), 126);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );

        assertEquals("Описание к фильму не может содержать более 200 символов!",
                validationException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmReleaseDateIsWrong() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                27), 126);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );

        assertEquals("Дата релиза не может быть раньше даты релиза первого фильма",
                validationException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmIfFilmReleaseDateIsMovieBirthday() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                28), 126);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmIfFilmReleaseDateIsAfterThenMovieBirthday() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1900, 12,
                26), 126);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmDurationIsNegative() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), -1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );

        assertEquals("Продолжительность фильма не может быть отрицаетльной",
                validationException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowNullPointerExceptionIfFilmIsNull() {
        final NullPointerException nullPointerException = assertThrows(
                NullPointerException.class,
                () -> filmController.addFilm(null)
        );

        assertEquals("Объект не может быть пустым",
                nullPointerException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void checkUpdateFilmIfValidationIsFine() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        Film newFilm1 = new Film(1, "Маска", "Один из дучших фильмов с Джимом Керри",
                LocalDate.of(2003, 3,
                        26), 126);
        assertEquals(0, filmController.getFilms().size());

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
        assertEquals("Один из дучших фильмов с Джимом Керри", newFilm1.getDescription());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmNameIsEmpty() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        Film newFilm1 = new Film(1, "", "Фильм на века", LocalDate.of(2004, 3,
                26), 126);
        filmController.addFilm(film1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(newFilm1)
        );

        assertEquals("Название фильма не может быть пустым!", validationException.getMessage());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void filmShouldUpdateIfDescriptionIs200Characters() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        Film newFilm1 = new Film(1, "Маска", "Фильм \"Маска\" -  это захватывающая комедия, где" +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы" +
                "Фильм смешной и непременно заставит вас улыбнуться.", LocalDate.of(2003, 3,
                26), 126);
        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Фильм \"Маска\" -  это захватывающая комедия, где" +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы" +
                "Фильм смешной и непременно заставит вас улыбнуться.", newFilm1.getDescription());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmDescriptionIsMoreThen200Characters() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        filmController.addFilm(film1);
        Film newFilm1 = new Film(1, "Маска", "Фильм Маска - это захватывающая комедийная" +
                "приключенческая история о скромном банковском служащем по имени Стэнли Ипкисс. Однажды он находит" +
                "древнюю маску, которая превращает его в эксцентричного супергероя. Стэнли начинает использовать свои" +
                "новые суперспособности для борьбы с преступностью и покорения сердца красавицы. Но с каждым днем он" +
                "понимает, что маска имеет свою темную сторону. Фильм полон юмора, экшена и неожиданных поворотов" +
                "сюжета. Не пропустите эту захватывающую историю о супергерое-неудачнике!",
                LocalDate.of(2003, 3, 26), 126);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(newFilm1)
        );

        assertEquals("Описание к фильму не может содержать более 200 символов!",
                validationException.getMessage());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmReleaseDateIsWrong() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        Film newFilm1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                27), 126);
        filmController.addFilm(film1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(newFilm1)
        );

        assertEquals("Дата релиза не может быть раньше даты релиза первого фильма",
                validationException.getMessage());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldUpdateFilmIfFilmReleaseDateIsMovieBirthday() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                28), 126);
        Film newFilm1 = new Film(1, "Маска", "Один из лучших фильмов с джимом Керри",
                LocalDate.of(1895, 12,
                        28), 126);

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldUpdateFilmIfFilmReleaseDateIsAfterThenMovieBirthday() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1900, 12,
                26), 126);
        Film newFilm1 = new Film(1, "Маска", "Один из лучших фильмов с джимом Керри",
                LocalDate.of(1895, 12,
                        28), 126);

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmDurationIsNegative() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        Film newFilm1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), -1);
        filmController.addFilm(film1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(newFilm1)
        );

        assertEquals("Продолжительность фильма не может быть отрицаетльной",
                validationException.getMessage());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void updateShouldThrowNullPointerExceptionIfFilmIsNull() {
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126);
        filmController.addFilm(film1);

        final NullPointerException nullPointerException = assertThrows(
                NullPointerException.class,
                () -> filmController.updateFilm(null)
        );

        assertEquals("Объект не может быть пустым",
                nullPointerException.getMessage());
        assertEquals(1, filmController.getFilms().size());
    }

    //
    @Test
    public void checkAddUserIfValidationIsFine() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        assertEquals(0, userController.getUsers().size());

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserEmailIsEmpty() {
        User user1 = new User(1, "", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals("Адрес алектронной почты не может быть пустым!", validationException.getMessage());
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserEmailNotContainsSpecialSymbol() {
        User user1 = new User(1, "veniamin.bestVitaminmail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("Адрес алектронной почты должен содержать символ @!", validationException.getMessage());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUsersLoginIsEmpty() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "", "Вениамин",
                LocalDate.of(1997, 6, 22));

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("Логин пользователя не может быть пустым!",
                validationException.getMessage());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUsersLoginContainsBlankSymbols() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Ve nya", "Вениамин",
                LocalDate.of(1997, 6, 22));

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("Логин пользователя не может содержать пробелы!",
                validationException.getMessage());
    }

    @Test
    public void shouldAddUserIfNameIsEmptyButLoginNotEmpty() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "",
                LocalDate.of(1997, 6, 22));

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("Venya", user1.getLogin());
        assertEquals("Venya", user1.getName());
    }

    @Test
    public void shouldAddUserIfBirthdayIsBeforeThenNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void shouldAddUserIfBirthdayIsNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.now());

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserBirthdayIfAfterThenNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2024, 6, 22));

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("День рождения пользователя не может быть больше текущей даты!",
                validationException.getMessage());
    }

    @Test
    public void addShouldThrowNullPointerExceptionIfUserIsNull() {
        final NullPointerException nullPointerException = assertThrows(
                NullPointerException.class,
                () -> userController.addUser(null)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("Объект не может быть пустым",
                nullPointerException.getMessage());
    }

    @Test
    public void checkUpdateUserIfValidationIsFine() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestThenVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        assertEquals(0, userController.getUsers().size());

        userController.addUser(user1);
        userController.updateUser(user1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("veniamin.bestThenVitamin@mail.ru", newUser1.getEmail());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUserEmailIsEmpty() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userController.addUser(user1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(newUser1)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Адрес алектронной почты не может быть пустым!", validationException.getMessage());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUserEmailNotContainsSpecialSymbol() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitaminmail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userController.addUser(user1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(newUser1)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Адрес алектронной почты должен содержать символ @!", validationException.getMessage());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUsersLoginIsEmpty() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userController.addUser(user1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(newUser1)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Логин пользователя не может быть пустым!",
                validationException.getMessage());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUsersLoginContainsBlankSymbols() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Ve nya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userController.addUser(user1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(newUser1)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Логин пользователя не может содержать пробелы!",
                validationException.getMessage());
    }

    @Test
    public void shouldUpdateUserIfNameIsEmptyButLoginNotEmpty() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", null,
                LocalDate.of(1997, 6, 22));

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("Venya", user1.getLogin());
        assertEquals("Venya", user1.getName());
    }

    @Test
    public void shouldUpdateUserIfBirthdayIsBeforeThenNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 3, 22));

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals(LocalDate.of(1997, 3, 22), user1.getBirthday());
    }

    @Test
    public void shouldUpdateUserIfBirthdayIsNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.now());

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals(LocalDate.now(), user1.getBirthday());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUserBirthdayIfAfterThenNow() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2021, 6, 22));
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2024, 6, 22));
        userController.addUser(user1);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(newUser1)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("День рождения пользователя не может быть больше текущей даты!",
                validationException.getMessage());
    }

    @Test
    public void updateShouldThrowNullPointerExceptionIfUserIsNull() {
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22));
        userController.addUser(user1);
        final NullPointerException nullPointerException = assertThrows(
                NullPointerException.class,
                () -> userController.updateUser(null)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Объект не может быть пустым",
                nullPointerException.getMessage());
    }

}

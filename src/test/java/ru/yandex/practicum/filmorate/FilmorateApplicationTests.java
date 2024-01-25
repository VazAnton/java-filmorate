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
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;
    public FilmStorage inMemoryFilmStorage;
    public UserStorage inMemoryUserStorage;
    public UserService userService;
    public FilmService filmService;

    @BeforeEach
    public void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(inMemoryFilmStorage, filmService);
        userController = new UserController(inMemoryUserStorage, userService);
    }

    @Test
    public void checkAddFilmIfValidationIsFine() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        assertEquals(0, filmController.getFilms().size());

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmNameIsEmpty() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );

        assertEquals("Название фильма не может быть пустым!", validationException.getMessage());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void addShouldNotThrowValidationExceptionIfFilmDescriptionIs200Characters() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм \"Маска\" -  это захватывающая комедия, где " +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы. " +
                "Фильм смешной и непременно заставит вас улыбнуться.", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmDescriptionIsMoreThen200Characters() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм Маска - это захватывающая комедийная " +
                "приключенческая история о скромном банковском служащем по имени Стэнли Ипкисс. Однажды он находит " +
                "древнюю маску, которая превращает его в эксцентричного супергероя. Стэнли начинает использовать свои " +
                "новые суперспособности для борьбы с преступностью и покорения сердца красавицы. Но с каждым днем он " +
                "понимает, что маска имеет свою темную сторону. Фильм полон юмора, экшена и неожиданных поворотов " +
                "сюжета. Не пропустите эту захватывающую историю о супергерое-неудачнике!",
                LocalDate.of(2003, 3,
                        26), 126, usersWhoLikeFilm);

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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                27), 126, usersWhoLikeFilm);

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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                28), 126, usersWhoLikeFilm);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmIfFilmReleaseDateIsAfterThenMovieBirthday() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1900, 12,
                26), 126, usersWhoLikeFilm);

        filmController.addFilm(film1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfFilmDurationIsNegative() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), -1, usersWhoLikeFilm);

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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Один из дучших фильмов с Джимом Керри",
                LocalDate.of(2003, 3,
                        26), 126, usersWhoLikeFilm);
        assertEquals(0, filmController.getFilms().size());

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
        assertEquals("Один из дучших фильмов с Джимом Керри", newFilm1.getDescription());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmNameIsEmpty() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "", "Фильм на века", LocalDate.of(2004, 3,
                26), 126, usersWhoLikeFilm);
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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Фильм \"Маска\" -  это захватывающая комедия, где" +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы" +
                "Фильм смешной и непременно заставит вас улыбнуться.", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Фильм \"Маска\" -  это захватывающая комедия, где" +
                "главный герой Стэнли Ипкисс случайно находит магическую маску, что дарует ему невероятные суперсилы" +
                "Фильм смешной и непременно заставит вас улыбнуться.", newFilm1.getDescription());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmDescriptionIsMoreThen200Characters() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        filmController.addFilm(film1);
        Film newFilm1 = new Film(1, "Маска", "Фильм Маска - это захватывающая комедийная" +
                "приключенческая история о скромном банковском служащем по имени Стэнли Ипкисс. Однажды он находит" +
                "древнюю маску, которая превращает его в эксцентричного супергероя. Стэнли начинает использовать свои" +
                "новые суперспособности для борьбы с преступностью и покорения сердца красавицы. Но с каждым днем он" +
                "понимает, что маска имеет свою темную сторону. Фильм полон юмора, экшена и неожиданных поворотов" +
                "сюжета. Не пропустите эту захватывающую историю о супергерое-неудачнике!",
                LocalDate.of(2003, 3, 26), 126, usersWhoLikeFilm);

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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                27), 126, usersWhoLikeFilm);
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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1895, 12,
                28), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Один из лучших фильмов с джимом Керри",
                LocalDate.of(1895, 12,
                        28), 126, usersWhoLikeFilm);

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldUpdateFilmIfFilmReleaseDateIsAfterThenMovieBirthday() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(1900, 12,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Один из лучших фильмов с джимом Керри",
                LocalDate.of(1895, 12,
                        28), 126, usersWhoLikeFilm);

        filmController.addFilm(film1);
        filmController.updateFilm(newFilm1);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfFilmDurationIsNegative() {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
        Film newFilm1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), -1, usersWhoLikeFilm);
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
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        assertEquals(0, userController.getUsers().size());

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserEmailIsEmpty() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals("Адрес алектронной почты не может быть пустым!", validationException.getMessage());
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserEmailNotContainsSpecialSymbol() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitaminmail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);

        final ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user1)
        );

        assertEquals(0, userController.getUsers().size());
        assertEquals("Адрес алектронной почты должен содержать символ @!", validationException.getMessage());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUsersLoginIsEmpty() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);

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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Ve nya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);

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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "",
                LocalDate.of(1997, 6, 22), friendsOfUser);

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("Venya", user1.getLogin());
        assertEquals("Venya", user1.getName());
    }

    @Test
    public void shouldAddUserIfBirthdayIsBeforeThenNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void shouldAddUserIfBirthdayIsNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.now(), friendsOfUser);

        userController.addUser(user1);

        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void addShouldThrowValidationExceptionIfUserBirthdayIfAfterThenNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2024, 6, 22), friendsOfUser);

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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestThenVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        assertEquals(0, userController.getUsers().size());

        userController.addUser(user1);
        userController.updateUser(user1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("veniamin.bestThenVitamin@mail.ru", newUser1.getEmail());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUserEmailIsEmpty() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitaminmail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Ve nya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", null,
                LocalDate.of(1997, 6, 22), friendsOfUser);

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals("Venya", user1.getLogin());
        assertEquals("Venya", user1.getName());
    }

    @Test
    public void shouldUpdateUserIfBirthdayIsBeforeThenNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 3, 22), friendsOfUser);

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals(LocalDate.of(1997, 3, 22), user1.getBirthday());
    }

    @Test
    public void shouldUpdateUserIfBirthdayIsNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.now(), friendsOfUser);

        userController.addUser(user1);
        userController.updateUser(newUser1);

        assertEquals(1, userController.getUsers().size());
        assertEquals(LocalDate.now(), user1.getBirthday());
    }

    @Test
    public void updateShouldThrowValidationExceptionIfUserBirthdayIfAfterThenNow() {
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2021, 6, 22), friendsOfUser);
        User newUser1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(2024, 6, 22), friendsOfUser);
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
        Set<Integer> friendsOfUser = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser);
        userController.addUser(user1);
        final NullPointerException nullPointerException = assertThrows(
                NullPointerException.class,
                () -> userController.updateUser(null)
        );

        assertEquals(1, userController.getUsers().size());
        assertEquals("Объект не может быть пустым",
                nullPointerException.getMessage());
    }

    @Test
    public void checkAddFriendWhenFriendExist() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);

        userController.addUser(user1);
        userController.addUser(user2);
        assertEquals(2, userController.getUsers().size());
        userController.addFriend(1, 2);

        assertEquals(1, user1.getFriendsOfUser().size());
    }

    @Test
    public void checkGetFriendsOfUserIfUserHaveFriend() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);

        userController.addUser(user1);
        userController.addUser(user2);
        assertEquals(2, userController.getUsers().size());
        userController.addFriend(1, 2);

        assertEquals(1, userController.getFriendsOfUser(1).size());
        assertEquals(1, userController.getFriendsOfUser(2).size());
    }

    @Test
    public void checkGetFriendsOfUserIfUserHaveNotFriend() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);

        userController.addUser(user1);
        assertEquals(1, userController.getUsers().size());

        assertEquals(0, userController.getFriendsOfUser(1).size());
    }

    @Test
    public void checkDeleteFriendIfFriendExist() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);

        userController.addUser(user1);
        userController.addUser(user2);
        assertEquals(2, userController.getUsers().size());
        userController.addFriend(1, 2);
        assertEquals(1, userController.getFriendsOfUser(2).size());
        userController.deleteFriend(1, 2);

        assertEquals(0, userController.getFriendsOfUser(1).size());
        assertEquals(0, userController.getFriendsOfUser(2).size());
    }

    @Test
    public void checkGetCommonFriendsIfUsersHaveCommonFriends() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        Set<Integer> friendsOfUser3 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);
        User user3 = new User(3, "Chicken.litle@yandex.ru", "ChickenLitle", "Кудах",
                LocalDate.of(2005, 11, 4), friendsOfUser3);

        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);
        assertEquals(3, userController.getUsers().size());
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);
        assertEquals(2, userController.getFriendsOfUser(1).size());
        assertEquals(2, userController.getFriendsOfUser(2).size());

        assertEquals(1, userController.getCommonFriends(1, 2).size());
    }

    @Test
    public void checkGetCommonFriendsIfOtherUsersNotExist() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);

        userController.addUser(user1);
        assertEquals(1, userController.getUsers().size());

        assertEquals(0, userController.getCommonFriends(1, 2).size());
    }

    @Test
    public void checkGetCommonFriendsIfUsersNotHaveCommonFriends() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        Set<Integer> friendsOfUser3 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);
        User user3 = new User(3, "Chicken.litle@yandex.ru", "ChickenLitle", "Кудах",
                LocalDate.of(2005, 11, 4), friendsOfUser3);

        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);
        assertEquals(3, userController.getUsers().size());
        userController.addFriend(1, 2);
        userController.addFriend(2, 3);
        assertEquals(2, userController.getFriendsOfUser(2).size());

        assertEquals(0, userController.getCommonFriends(2, 1).size());
    }

    @Test
    public void checkLikeFilmIfChosenFilmAndUserExist() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);

        userController.addUser(user1);
        assertEquals(1, userController.getUsers().size());
        filmController.addFilm(film1);
        assertEquals(1, filmController.getFilms().size());
        filmController.like(1, 1);

        assertEquals(1, film1.getUsersWhoLikeFilm().size());
    }

    @Test
    public void checkDeleteLikeFilmIfChosenFilmAndUserExist() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm);

        userController.addUser(user1);
        assertEquals(1, userController.getUsers().size());
        filmController.addFilm(film1);
        assertEquals(1, filmController.getFilms().size());
        filmController.like(1, 1);
        assertEquals(1, film1.getUsersWhoLikeFilm().size());
        filmController.deleteLike(1, 1);

        assertEquals(0, film1.getUsersWhoLikeFilm().size());
    }

    @Test
    public void checkGetTopFilmsIfFilmsMapNotEmpty() {
        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        Set<Integer> friendsOfUser3 = new HashSet<>();
        Set<Integer> usersWhoLikeFilm1 = new HashSet<>();
        Set<Integer> usersWhoLikeFilm2 = new HashSet<>();
        User user1 = new User(1, "veniamin.bestVitamin@mail.ru", "Venya", "Вениамин",
                LocalDate.of(1997, 6, 22), friendsOfUser1);
        User user2 = new User(2, "Tolik.anabolik@yandex.ru", "Anatolik", "Анатолий",
                LocalDate.of(1996, 4, 25), friendsOfUser2);
        User user3 = new User(3, "Chicken.litle@yandex.ru", "ChickenLitle", "Кудах",
                LocalDate.of(2005, 11, 4), friendsOfUser3);
        Film film1 = new Film(1, "Маска", "Фильм на века", LocalDate.of(2003, 3,
                26), 126, usersWhoLikeFilm1);
        Film film2 = new Film(2, "Титаник", "Фильм - катастрофа",
                LocalDate.of(1998, 2, 20), 194, usersWhoLikeFilm2);

        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);
        assertEquals(3, userController.getUsers().size());
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        assertEquals(2, filmController.getFilms().size());
        filmController.like(1, 1);
        filmController.like(1, 2);
        filmController.like(2, 3);
        assertEquals(2, film1.getUsersWhoLikeFilm().size());
        assertEquals(1, film2.getUsersWhoLikeFilm().size());

        assertEquals(2, filmController.getTopFilms(2).size());
    }

    @Test
    public void checkGetTopFilmsIfFilmsMapEmpty() {
        filmController.getTopFilms(12);

        assertEquals(0, filmController.getTopFilms(12).size());
    }

    @Test
    public void getUserShouldThrowNullPointerExceptionIfUserIdNotExist() {
        assertThrows(NullPointerException.class, () -> userController.getUser(1));
    }

    @Test
    public void getFilmShouldThrowNullPointerExceptionIfFilmIdNotExist() {
        assertThrows(NullPointerException.class, () -> filmController.getFilm(1));
    }
}

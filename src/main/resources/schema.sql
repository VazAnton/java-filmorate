DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS film_director;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS feed;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS genres
(
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS ratings
(
rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(10)
);

CREATE TABLE IF NOT EXISTS directors
(
director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email varchar(50) NOT NULL,
login varchar(20) NOT NULL,
name varchar(20),
birthday date NOT NULL
CONSTRAINT email_not_empty_check CHECK(email <> ''),
CONSTRAINT login_not_empty_check CHECK(login <> '')
);

CREATE TABLE IF NOT EXISTS friends
(
user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
friend_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
status_of_friendship BOOLEAN NOT NULL DEFAULT FALSE,
PRIMARY KEY(user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS films
(
film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(50) NOT NULL,
description varchar(200) NOT NULL,
release_date date NOT NULL,
duration INTEGER,
rating_id INTEGER REFERENCES ratings(rating_id)
CONSTRAINT duration_check CHECK(duration > 0)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     varchar(200) NOT NULL,
    is_positive BOOLEAN      NOT NULL,
    user_id     INTEGER REFERENCES users (user_id),
    film_id     INTEGER REFERENCES films (film_id),
    useful      INTEGER      NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
genre_id INTEGER REFERENCES genres(genre_id) ON DELETE CASCADE,
PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes
(
film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS feed
(
    event_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp  LONG,
    user_id    INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    event_type varchar(50) NOT NULL,
    operation  varchar(50) NOT NULL,
    entity_id  INTEGER
);

CREATE TABLE IF NOT EXISTS film_director
(
film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
director_id INTEGER REFERENCES directors(director_id) ON DELETE CASCADE
);
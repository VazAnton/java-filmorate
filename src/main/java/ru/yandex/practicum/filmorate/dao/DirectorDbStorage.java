package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage.DirectorStorage;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Director> getDirectorMapper() {
        return ((rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("name")));
    }

    @Override
    public Director addDirector(Director director) {
        if (director != null) {
            SimpleJdbcInsert simpleDirectorInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            int id = simpleDirectorInsert.executeAndReturnKey(
                    Map.of("name", director.getName())).intValue();
            director.setId(id);
        }
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (director != null && getDirector(director.getId()) != null) {
            jdbcTemplate.update("UPDATE directors set name = ? WHERE director_id = ?;", director.getName(),
                    director.getId());
            return getDirector(director.getId());
        }
        return director;
    }

    @Override
    public Director getDirector(int id) {
        return jdbcTemplate.queryForObject("SELECT* FROM directors WHERE director_id = ?;", getDirectorMapper(), id);
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT* FROM directors;", getDirectorMapper());
    }

    @Override
    public boolean deleteDirector(int id) {
        if (getDirector(id) != null) {
            jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?;", id);
            return true;
        }
        return false;
    }
}

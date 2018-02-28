package services.dao;


import services.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void register(@NotNull User userToRegister) throws DataAccessException {
        final String sql = "INSERT INTO \"User\" (email, nickname, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userToRegister.getEmail(), userToRegister.getNickname(), userToRegister.getPassword());
    }


    public Boolean login(@NotNull User userToLogin) {
        try {
            final String sql = "SELECT * FROM \"User\" WHERE email = ?::citext";
            final User user = jdbcTemplate.queryForObject(sql, new Object[]{userToLogin.getEmail()}, new UserMapper());

            if (user.getPassword().equals(userToLogin.getPassword())) {
                return true;
            }
        } catch (DataAccessException e) {
            return false;
        }

        return false;
    }

    public User getUser(@NotNull String nickname) throws DataAccessException {
        final String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
        return jdbcTemplate.queryForObject(sql, new Object[]{nickname}, new UserMapper());
    }

    public void updateUser(@NotNull User userToUpdate, @NotNull String oldEmail) throws DataAccessException {
        final Boolean hasEmail = userToUpdate.getEmail() != null && !userToUpdate.getEmail().isEmpty();
        final Boolean hasPassword = userToUpdate.getPassword() != null && !userToUpdate.getPassword().isEmpty();
        final Boolean hasRating = userToUpdate.getRating() != null && userToUpdate.getRating() >= 0;
        final Boolean condition = hasEmail || hasPassword || hasRating;

        if (condition) {
            final List<Object> sqlParameters = new ArrayList<>();
            final StringBuilder sql = new StringBuilder("UPDATE \"User\" SET");

            if (hasEmail) {
                sql.append(" nickname = ?::citext");
                sqlParameters.add(userToUpdate.getEmail());
            }

            if (hasPassword) {
                sql.append(" password = ?::citext");
                sqlParameters.add(userToUpdate.getPassword());
            }

            if (hasRating) {
                sql.append(" rating = ?");
                sqlParameters.add(userToUpdate.getRating());
            }

            sql.append(" WHERE email = ?::citext;");
            sqlParameters.add(oldEmail);
            jdbcTemplate.update(sql.toString(), sqlParameters.toArray());
        }
    }


    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int incParam) throws SQLException {
            final User user = new User();
            user.setEmail(resultSet.getString("email"));
            user.setNickname(resultSet.getString("nickname"));
            user.setPassword(resultSet.getString("password"));

            return user;
        }
    }
}

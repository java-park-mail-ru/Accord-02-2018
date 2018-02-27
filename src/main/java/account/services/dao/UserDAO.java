package account.services.dao;


import account.services.model.User;
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

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void register(@NotNull User userToRegister) throws DataAccessException {
        String sql = "INSERT INTO \"User\" (email, nickname, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userToRegister.getEmail(), userToRegister.getNickname(), userToRegister.getPassword());
    }


    public Boolean login(@NotNull User userToLogin) {
        try {
            String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
            User user = jdbcTemplate.queryForObject(sql, new Object[]{userToLogin.getNickname()}, new UserMapper());

            if (user.getPassword().equals(userToLogin.getPassword())) {
                return true;
            }
        } catch (DataAccessException e) {
            return false;
        }

        return false;
    }

    public User getUser(@NotNull User userToGet) throws DataAccessException {
        String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
        User user = jdbcTemplate.queryForObject(sql, new Object[]{userToGet.getNickname()}, new UserMapper());
        return user;
    }

    public User getUser(@NotNull String nickname) throws DataAccessException {
        String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
        User user = jdbcTemplate.queryForObject(sql, new Object[]{nickname}, new UserMapper());
        return user;
    }

    public void updateUser(@NotNull User userToUpdate) throws DataAccessException {
        final Boolean hasEmail = userToUpdate.getEmail() != null && !userToUpdate.getEmail().isEmpty();
        final Boolean hasPassword = userToUpdate.getPassword() != null && !userToUpdate.getPassword().isEmpty();
        final Boolean hasRating = userToUpdate.getRating() != null && userToUpdate.getRating() >= 0;
        final Boolean condition = hasEmail || hasPassword || hasRating;

        if (condition) {
            final List<Object> sqlParameters = new ArrayList<>();
            StringBuilder sql = new StringBuilder("UPDATE \"User\" SET");

            if (hasEmail) {
                sql.append(" email = ?::citext");
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

            sql.append(" WHERE nickname = ?::citext;");
            sqlParameters.add(userToUpdate.getNickname());
            jdbcTemplate.update(sql.toString(), sqlParameters.toArray());
        }
    }


    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setEmail(resultSet.getString("email"));
            user.setNickname(resultSet.getString("nickname"));
            user.setPassword(resultSet.getString("password"));

            return user;
        }
    }
}

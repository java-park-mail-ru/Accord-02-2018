package UserServices.DAO;


import UserServices.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void register(String email, String nickname, String password) throws DataAccessException {
        String sql = "INSERT INTO \"User\" (email, nickname, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, email, nickname, password);
    }


    public Boolean login(User userToLogin){
        try {
            String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
            User user = jdbcTemplate.queryForObject(sql, new Object[]{userToLogin.getNickname()}, new UserMapper());

            if( user.getPassword().equals(userToLogin.getPassword()) ){
                return true;
            }
        } catch (DataAccessException e) {
            return false;
        }

        return false;
    }

    public User getUser(String nickname) throws DataAccessException {
        String sql = "SELECT * FROM \"User\" WHERE nickname = ?::citext";
        User user = jdbcTemplate.queryForObject(sql, new Object[]{nickname}, new UserMapper());
        return user;
    }

    public void updateUser(User user) throws DataAccessException {
        final Boolean hasEmail = user.getEmail() != null && !user.getEmail().isEmpty();
        final Boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();
        final Boolean hasRating = user.getRating() != null && user.getRating() >= 0;
        final Boolean condition = hasEmail || hasPassword || hasRating;

        if (condition) {
            final List<Object> sqlParameters = new ArrayList<>();
            StringBuilder sql = new StringBuilder("UPDATE \"User\" SET");

            if (hasEmail) {
                sql.append(" email = ?::citext");
                sqlParameters.add(user.getEmail());
            }

            if (hasPassword) {
                sql.append(" password = ?::citext");
                sqlParameters.add(user.getPassword());
            }

            if (hasRating) {
                sql.append(" rating = ?");
                sqlParameters.add(user.getRating());
            }

            sql.append(" WHERE nickname = ?::citext;");
            sqlParameters.add(user.getNickname());
            jdbcTemplate.update( sql.toString(), sqlParameters.toArray() );
        }
    }


    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setEmail(resultSet.getString("email"));
            user.setNickname(resultSet.getString("nickname"));
            user.setPassword(resultSet.getString("password"));
            user.setId(resultSet.getInt("id"));

            return user;
        }
    }
}

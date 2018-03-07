package services.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import services.model.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserInfoDAO {
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UserInfo> getSortedUsersInfoByRating(int userPerPage, int page) {
        try {
            final int offset = (page - 1) * userPerPage;
            final String sql = "SELECT * FROM \"User\" ORDER BY rating DESC LIMIT ? OFFSET ?;";
            return jdbcTemplate.query(sql, new Object[]{userPerPage, offset}, new UserInfoMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int getLastPage(int userPerPage) {
        try {
            final int numberOfUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"User\";", Integer.class);
            return numberOfUsers / userPerPage + 1;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    public static class UserInfoMapper implements RowMapper<UserInfo> {
        @Override
        public UserInfo mapRow(ResultSet resultSet, int incParam) throws SQLException {
            final UserInfo userInfo = new UserInfo();
            userInfo.setNickname(resultSet.getString("nickname"));
            userInfo.setRating(resultSet.getInt("rating"));

            return userInfo;
        }
    }
}

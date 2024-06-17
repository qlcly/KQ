package com.example.demo.webservice.repository;

import com.example.demo.data.user.UserBo;
import com.example.demo.data.user.UserDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("userRowMapper")
    private RowMapper<UserDo> rowMapper;

    public UserDo queryUserById(long id)
    {
        String sql = "SELECT * FROM `user` WHERE id=? ";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public List<UserDo> queryAllUser()
    {
        String sql = "SELECT * FROM `user`";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void insertUser(UserBo user)
    {
        String sql = "INSERT INTO `user` (id, `name`, password , auth, userType) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getName(), user.getPassword(),user.getAuth(),user.getType());
    }

    public UserDo updateUserById(long id, UserBo user)
    {
        String sql = "UPDATE `user` set `name`= ? ,password = ?,auth = ?,userType= ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getPassword(), user.getAuth(),user.getType(), id);
        return queryUserById(id);
    }

    public void deleteUserById(long id)
    {
        String sql = "DELETE FROM `user` WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}

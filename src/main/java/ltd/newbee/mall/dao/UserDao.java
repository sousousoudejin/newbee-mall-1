package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    List<User> findAllUsers();

    int insertUser(User user);

    int updUser(@Param("user") User user);

    int delUser(Integer id);

    User findUserById(Integer id);

    int findUserCount(Integer id);

}

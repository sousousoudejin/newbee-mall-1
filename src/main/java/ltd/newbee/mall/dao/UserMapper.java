package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface UserMapper {
    List<User> findUsers(PageQueryUtil pageQueryUtil);

    int getTotalUser();
}

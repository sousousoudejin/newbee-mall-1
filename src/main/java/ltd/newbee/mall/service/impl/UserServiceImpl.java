package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public PageResult getUserPage(PageQueryUtil pageQueryUtil) {

        List<User> userList = userMapper.findUsers(pageQueryUtil);

        int totalCount = userMapper.getTotalUser();

        return new PageResult(userList,totalCount,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }
}

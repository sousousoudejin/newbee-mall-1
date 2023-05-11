package ltd.newbee.mall.controller;

import ltd.newbee.mall.dao.UserDao;
import ltd.newbee.mall.entity.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/mybatis")
public class MybatisController {

    //注入dao层
    @Resource
    private UserDao userDao;

    //查看所有user
    @GetMapping("/findAll")
    public List<User> findAllUsers() {

        List<User> allUsers = userDao.findAllUsers();

        return allUsers;

    }

    //插入一条用户信息
    @PostMapping("/insertUser")
    public String insertUser(User user) {

        if (user == null && StringUtils.hasText(user.getName()) && StringUtils.hasText(user.getPassword())) {

            return "请重新校验参数后，在进行创建";

        }

        int i = userDao.insertUser(user);

        return i == 1 ? "插入成功" : "插入失败";


    }

    //修改某行信息
    @PostMapping("/updUser")
    public String updUser(User user) {

        if (user == null && user.getId() > 0 && (StringUtils.hasText(user.getName()) || StringUtils.hasText(user.getPassword()))) {

            return "请重新校验参数-修改";

        }

        int i = userDao.updUser(user);

        return i == 1 ? "修改成功" : "修改失败";
    }

    //删除一条数据
    @GetMapping("/delUser")
    public String delUser(Integer id) {

        if (id < 1) {
            return "删除失败";
        }

        int i = userDao.findUserCount(id);

        if (i < 1) {

            return "删除失败";

        }

        int i1 = userDao.delUser(id);

        return i1 > 0 ? "删除成功" : "删除失败";

    }

}

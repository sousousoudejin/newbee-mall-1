package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.AdminUser;

public interface AdminUserMapper {

    AdminUser login(String userName, String password);

    AdminUser selectByPrimaryKey(Integer loginUserId);

    int updateByPrimaryKeySelective(AdminUser adminUser);
}

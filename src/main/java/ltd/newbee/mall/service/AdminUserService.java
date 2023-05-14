package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserService {
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser getUserDetailById(Integer loginUserId);

    boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    boolean updateName(Integer loginUserId, String loginUserName, String nickName);
}

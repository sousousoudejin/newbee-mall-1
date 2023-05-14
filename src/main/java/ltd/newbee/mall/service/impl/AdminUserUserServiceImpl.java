package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.AdminUserMapper;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import ltd.newbee.mall.util.MD5Util;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class AdminUserUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String userName, String password) {
        String pass = MD5Util.MD5Encode(password,"utf-8");
        return adminUserMapper.login(userName,pass);

    }

    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {

        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);

        if (adminUser != null) {

            String originalPass = MD5Util.MD5Encode(originalPassword,"utf-8");
            String newPass = MD5Util.MD5Encode(newPassword,"utf-8");

            if (originalPass.equals(adminUser.getLoginPassword())) {

                adminUser.setLoginPassword(newPass);

                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) return true;

            }
        }
        return false;

    }

    @Override
    public boolean updateName(Integer loginUserId, String loginUserName, String nickName) {

        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);

        if (StringUtils.hasText(loginUserName)) adminUser.setLoginUserName(loginUserName);

        if (StringUtils.hasText(nickName)) adminUser.setNickName(nickName);

        if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) return true;

        return false;
    }


}

package ltd.newbee.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.service.NewBeeMallUserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.util.NewBeeMallUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {

    @Autowired
    private MallUserMapper userMapper;


    @Override
    public String login(String loginName, String passwordMd5, HttpSession httpSession) {
        MallUser user = userMapper.selectByLoginNameAndPasswd(loginName,passwordMd5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag()
                    == 1) return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();

            if (user.getNickName().length() > 7) {
                String newName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(newName);
            }

            NewBeeMallUserVO userVO = new NewBeeMallUserVO();
            BeanUtil.copyProperties(user,userVO);
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY,userVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.ERROR.getResult();
    }

    @Override
    public String register(String loginName, String password) {
        if (userMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (userMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        NewBeeMallUserVO userTemp = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = userMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            if (!StringUtils.isEmpty(mallUser.getNickName())) {
                userFromDB.setNickName(NewBeeMallUtils.cleanString(mallUser.getNickName()));
            }
            if (!StringUtils.isEmpty(mallUser.getAddress())) {
                userFromDB.setAddress(NewBeeMallUtils.cleanString(mallUser.getAddress()));
            }
            if (!StringUtils.isEmpty(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(mallUser.getIntroduceSign()));
            }
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
                userFromDB = userMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(userFromDB, newBeeMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
                return newBeeMallUserVO;
            }
        }
        return null;
    }


}

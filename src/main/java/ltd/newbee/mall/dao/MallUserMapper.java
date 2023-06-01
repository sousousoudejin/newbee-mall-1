package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.MallUser;

public interface MallUserMapper {


    MallUser selectByLoginNameAndPasswd(String loginName, String passwordMd5);

    MallUser selectByLoginName(String loginName);

    int insertSelective(MallUser registerUser);

    int updateByPrimaryKeySelective(MallUser userFromDB);

    MallUser selectByPrimaryKey(Long userId);
}

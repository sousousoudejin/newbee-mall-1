package ltd.newbee.mall.service;

import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.entity.MallUser;

import javax.servlet.http.HttpSession;

public interface NewBeeMallUserService {
    String login(String loginName, String s, HttpSession httpSession);

    String register(String loginName, String password);

    NewBeeMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);
}

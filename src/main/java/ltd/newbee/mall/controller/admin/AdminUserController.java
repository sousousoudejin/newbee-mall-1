package ltd.newbee.mall.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/login")
    public String login() {
        return "/admin/login";
    }

    /**
     * 测试 搭建项目跳转index
     * @return
     */
    @GetMapping("/index")
    public String indexAll() {
        return "/admin/index";
    }

    /**
     * 测试验证码 校验
     * @param httpSession
     * @param code
     * @return
     */
    @GetMapping("/verify")
    @ResponseBody
    public String verifyCode(HttpSession httpSession,@RequestParam("code") String code) {

        if (!StringUtils.hasText(code)) return "验证码为空！";

        ShearCaptcha sc = (ShearCaptcha) httpSession.getAttribute("verifyCode");

        if (sc == null || !sc.verify(code)) return "验证码校验失败！";

        return "登录成功！！！";

    }

    /**
     * 后台管理系统登录
     * @param session
     * @param userName
     * @param password
     * @param verifyCode
     * @return
     */
    @PostMapping("/login")
    public String login(HttpSession session, @RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode) {

        //验证验证码不为空
        if (!StringUtils.hasText(verifyCode)) {
            session.setAttribute("errorMsg","验证码不能为空！");
            return "/admin/login";
        }

        //验证用户名/密码不为空
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            session.setAttribute("errorMsg","用户名或密码不能为空！");
            return "/admin/login";
        }

        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        //校验验证码正确性
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            session.setAttribute("errorMsg","验证码校验失败！");
            return "/admin/login";
        }

        AdminUser adminUser = adminUserService.login(userName,password);

        if (adminUser == null) {
            session.setAttribute("errorMsg","用户名或密码错误！！！");
            return "/admin/login";

        }

        session.setAttribute("loginUser",adminUser.getNickName());
        session.setAttribute("loginUserId",adminUser.getAdminUserId());
        return "redirect:/admin/index";
    }


    /**
     * 跳转 修改密码界面
     * @param request
     * @return
     */
    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {

        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");

        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);

        if (adminUser == null) return "/admin/login";

        request.getSession().setAttribute("loginUserName",adminUser.getLoginUserName());
        request.getSession().setAttribute("nickName",adminUser.getNickName());
        request.getSession().setAttribute("path","profile");

        return "/admin/profile";

    }

    /**
     * 修改密码
     * @param httpSession
     * @param originalPassword
     * @param newPassword
     * @return
     */
    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpSession httpSession,
                                 @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){

        if (!StringUtils.hasText(originalPassword) && !StringUtils.hasText(newPassword)) return "字符无效，请重新输入。";

        Integer loginUserId = (Integer) httpSession.getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId,originalPassword,newPassword)) {
            httpSession.removeAttribute("loginUser");
            httpSession.removeAttribute("loginUserId");
            httpSession.removeAttribute("errorMsg");
            return "success";
        } else return "修改失败";
    }

    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpSession session,
                             @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {

        if (!StringUtils.hasText(loginUserName) || !StringUtils.hasText(nickName)) return "请至少修改一处名称";

        Integer loginUserId = (Integer) session.getAttribute("loginUserId");

        if (adminUserService.updateName(loginUserId,loginUserName,nickName)) return "success";

        return "修改失败";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginUserId");
        session.removeAttribute("loginUser");
        session.removeAttribute("errorMsg");
        return "/admin/login";
    }

}

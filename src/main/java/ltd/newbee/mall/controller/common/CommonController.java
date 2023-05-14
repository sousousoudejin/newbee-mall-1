package ltd.newbee.mall.controller.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import ltd.newbee.mall.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * newbee-mall 验证码
 */
@Controller
@RequestMapping("/common")
public class CommonController {

    /**
     * admin 商城后台-生成验证码
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @GetMapping("/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        ServletOutputStream outputStream = null;

        try {
            httpServletResponse.setHeader("Cache-Control", "no-store");
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setDateHeader("Expires", 0);
            httpServletResponse.setContentType("image/png");
            outputStream = httpServletResponse.getOutputStream();

            ShearCaptcha shearCaptcha= CaptchaUtil.createShearCaptcha(150, 30, 4, 2);

            httpServletRequest.getSession().setAttribute("verifyCode",shearCaptcha);

            shearCaptcha.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @GetMapping("/mall/kaptcha")
    public void mallKaptcha(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {

        ServletOutputStream outputStream = null;

        try {
            httpServletResponse.setHeader("Cache-Control", "no-store");
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setDateHeader("Expires", 0);
            httpServletResponse.setContentType("image/png");
            outputStream = httpServletResponse.getOutputStream();

            ShearCaptcha shearCaptcha= CaptchaUtil.createShearCaptcha(110, 40, 4, 2);

            httpServletRequest.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY,shearCaptcha);

            shearCaptcha.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}

package com.zhengjianting.autosoftware.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zhengjianting.autosoftware.common.exception.CaptchaException;
import com.zhengjianting.autosoftware.common.lang.Const;
import com.zhengjianting.autosoftware.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图片验证码校验过滤器，在登录过滤器前
 */
@Slf4j
@Component
public class CaptchaFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean success = true;
        if ("/login".equals(request.getRequestURI()) && request.getMethod().equals("POST")) {
            try {
                // 验证码校验
                validateCaptcha(request);
                log.info("验证码验证成功");
            } catch (CaptchaException e){
                log.error("验证码验证错误！");
                // 验证码校验失败则交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(request, response, e);
                success = false; // 验证不通过
            }
        }
        if (success) {
            // 验证码校验通过后交给过滤器链下一个继续处理
            filterChain.doFilter(request, response);
        }
    }

    // 验证码校验
    private void validateCaptcha(HttpServletRequest httpServletRequest) {
        String captcha = httpServletRequest.getParameter("captcha");
        String captchaKey = httpServletRequest.getParameter("captchaKey");
        if (StringUtils.isBlank(captcha) || StringUtils.isBlank(captchaKey)) {
            throw new CaptchaException("验证码不能为空！");
        }
        if (!captcha.equals(redisUtil.hget(Const.CAPTCHA_KEY, captchaKey))) {
            throw new CaptchaException("验证码验证错误！");
        }
        // 验证一次就删除
        redisUtil.hdel(Const.CAPTCHA_KEY, captchaKey);
    }
}

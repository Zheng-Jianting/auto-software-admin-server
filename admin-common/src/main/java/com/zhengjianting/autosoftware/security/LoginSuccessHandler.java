package com.zhengjianting.autosoftware.security;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.service.impl.UserService;
import com.zhengjianting.autosoftware.util.JwtPayload;
import com.zhengjianting.autosoftware.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();

        // 登录成功即security认证成功, /login访问的责任链结束, 就可以生成jwt并放到请求头中返回给前端
        User user = userService.getByUsername(authentication.getName());

        // JwtPayload存放登录用户id和名称
        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.setUserId(user.getId());
        jwtPayload.setUsername(authentication.getName());

        // 生成token并将token放在响应头
        String token = jwtUtil.generateToken(jwtPayload);
        response.setHeader(jwtUtil.getHeader(), token);

        // 获取当前登录用户: 角色编码 & 权限编码
        String authorityInfo = userService.getUserAuthorityInfo(user.getId());
        String[] rolePermission = StringUtils.tokenizeToStringArray(authorityInfo, ",");

        log.info("用户：" + user.getUsername() + "登陆成功");
        Result result = new Result(200, "login success", MapUtil.builder().put("user", user).put("rolePermission", rolePermission).build());
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

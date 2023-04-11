package com.zhengjianting.autosoftware.security;

import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.common.lang.Result;
import com.zhengjianting.autosoftware.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    @Resource
    private JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 就是说虽然前后端都清空了token, 但是这个token其实依旧有效并不是真正意义上的清除
        if (authentication != null){
            // 后端手动退出
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        // 告诉前端
        response.setContentType("application/json;charset=utf-8");
        // 清空请求头中的jwt
        response.setHeader(jwtUtil.getHeader(),"");

        log.info("用户退出登录");
        Result result = Result.success("退出成功");

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

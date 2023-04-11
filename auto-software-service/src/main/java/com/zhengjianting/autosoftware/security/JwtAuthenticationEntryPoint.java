package com.zhengjianting.autosoftware.security;

import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证异常处理器,发生在jwt过滤器认证失败（异常）之后处理
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 只需要设置错误的认证状态码401, 禁止访问, 前端可以在请求头获得该状态码, 而不是data中的状态码
        ServletOutputStream outputStream = response.getOutputStream();
        Result result = Result.fail(HttpServletResponse.SC_UNAUTHORIZED,"请先登录！");
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

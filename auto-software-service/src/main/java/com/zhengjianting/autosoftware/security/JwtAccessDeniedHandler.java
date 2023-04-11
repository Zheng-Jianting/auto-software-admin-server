package com.zhengjianting.autosoftware.security;

import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 权限认证异常处理器
 * 发生在鉴权管理器处理后，无权限进入该处理器，有权限进入control层
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 只需要设置认证权限不足的状态码403, 前端可以在请求头获得该状态码
        ServletOutputStream outputStream = response.getOutputStream();
        log.error("权限不足：" + accessDeniedException.getMessage());
        Result result = Result.fail(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}

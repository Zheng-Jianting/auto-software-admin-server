package com.zhengjianting.autosoftware.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.util.JwtPayload;
import com.zhengjianting.autosoftware.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    @Resource
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailServiceImpl userDetailService;

    // 构造方法
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(jwtUtil.getHeader());

        // 不带jwt token
        if (StrUtil.isBlankOrUndefined(token)) {
            // 不带jwt token就不走此过滤器验证token, 让它去匿名访问下一个处理器
            // 后面还会有异常处理器（AuthenticationEntryPoint、AccessDeniedHandler）、权限认证的鉴权器等处理器会处理
            // 如AuthenticationEntryPoint会处理没有令牌的情况异常，而权限认证处理器看是否是白名单中的url（即访问白名单的接口有错误token时会在下面报错），如果不是白名单中的url（即没有权限访问该url），没有令牌也会处理异常
            chain.doFilter(request, response);
            return;
        }

        // 带token, 解析token并交给security认证
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null) {
            log.error("token异常");
            throw new JwtException("token异常");
        }
        if (jwtUtil.isTokenExpired(claims)) {
            log.error("token过期");
            throw new JwtException("token过期");
        }

        // 通过token获取账号信息，设置令牌将用户账号、角色权限等信息并交给security
        String str = JSONUtil.toJsonStr(claims.get("payload"));
        JwtPayload jwtPayload = JSONUtil.toBean(str, JwtPayload.class);
        Long userId = jwtPayload.getUserId();
        String username = jwtPayload.getUsername();

        // 获取角色和权限列表
        List<GrantedAuthority> grantedAuthorities = userDetailService.getUserAuthority(userId);

        // security进行用户账号和密码认证（Account在UserDetailsImpl重写的时候已经交给了security，这里只需要提交username即可进行身份对比认证），绑定用户角色和操作权限列表。
        // 后面我们只需要在Controller添加上具体注解表示需要的权限，Security就会自动帮我们自动完成权限校验了。
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }
}

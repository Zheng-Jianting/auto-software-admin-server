package com.zhengjianting.autosoftware.config;

import com.zhengjianting.autosoftware.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 进入方法都需要进行权限检验
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] URL_WHITELIST = { "/login", "/logout", "/captchaImage", "/user/saveNormal", "/thirdInterface/*", "/test/*" };

    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private LoginFailureHandler loginFailureHandler;
    @Resource
    private JwtLogoutSuccessHandler jwtLogoutSuccessHandler;
    @Resource
    private CaptchaFilter captchaFilter;

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager());
    }

    @Resource
    private UserDetailServiceImpl userDetailService;
    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Resource
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 告诉security数据库使用的密码加密方式, Security内置了的BCryptPasswordEncoder里面就有生成和匹配密码是否正确的方法, 也就是加密和验证策略
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 跨域
        http.cors().and().csrf().disable()
                // 登录配置
                .formLogin().failureHandler(loginFailureHandler).successHandler(loginSuccessHandler)

                // 退出配置
                .and().logout().logoutSuccessHandler(jwtLogoutSuccessHandler)
                // 禁用session
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 配置拦截规则
                .and().authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll() // 白名单都允许通过
                .anyRequest().authenticated() // 任何其他请求都需要认证

                // 异常处理器
                .and().exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 配置自定义的过滤器
                .and()
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class) // 验证码校验过滤器放在登录认证前
                .addFilter(jwtAuthenticationFilter());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }
}

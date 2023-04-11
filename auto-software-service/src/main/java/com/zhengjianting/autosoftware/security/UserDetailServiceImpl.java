package com.zhengjianting.autosoftware.security;

import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.service.impl.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    // 实现UserDetailsService拿到UserDetail后，security的authenticationManager就可以通过这个UserDetail进行对比身份认证
    @Resource
    private UserService userService;

    /**
     * 自定义User账号认证
     * 该方法在登录认证的认证管理器中进行
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) {
            // 不用给"用户"具体提示哪个错。防止“用户”知道另一个对了,写‘用户名或密码不正确’是为了防止一些黑客的攻击。
            // 只需验证账号
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
        // 密码认证交给security的authenticationManager
        return new AccountUser(user.getId(), user.getUsername(), user.getPassword(), getUserAuthority(user.getId()));
    }

    /**
     * 获取用户权限信息: 角色编码 & 权限编码
     */
    public List<GrantedAuthority> getUserAuthority(Long userId){
        String authority = userService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}

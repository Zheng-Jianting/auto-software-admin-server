package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.Permission;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.mapper.UserMapper;
import com.zhengjianting.autosoftware.service.UserServiceI;
import com.zhengjianting.autosoftware.util.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceImpl<UserMapper, User> implements UserServiceI {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RoleService roleService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PermissionService permissionService;

    @Override
    public User getByUsername(String username) {
        return getOne(new QueryWrapper<User>().eq("delete_flag", "N").eq("username", username));
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
        // role_user,role_admin,login-mgmt,user-mgmt,...
        String authority = "";
        String authorityKey = "GrantedAuthority:" + userId;

        if (redisUtil.hasKey(authorityKey)) {
            authority = (String) redisUtil.get(authorityKey);
        } else {
            /**
             * 获取角色编码
             *
             * select *
             * from   user_mgmt.role_t
             * where  delete_flag = 'N'
             * and    id in (
             *      select role_id
             *      from   user_mgmt.user_role_t
             *      where  delete_flag = 'N'
             *      and    user_id = #{userId}
             * )
             */
            List<Role> roles = roleService.list(new QueryWrapper<Role>().eq("delete_flag", "N").inSql("id", "select role_id from user_mgmt.user_role_t where delete_flag = 'N' and user_id = " + userId));
            String roleCodes = roles.stream().map(role -> "role_" + role.getRoleCode()).collect(Collectors.joining(","));

            /**获取权限编码
             *
             * SELECT DISTINCT rm.permission_id
             * FROM   user_mgmt.user_role_t ur
             * JOIN   user_mgmt.role_permission_t rm
             * ON     rm.delete_flag = 'N'
             * and    ur.role_id = rm.role_id
             * WHERE  ur.delete_flag = 'N'
             * and    ur.user_id = #{userId}
             */
            List<Long> permissionIdList = userMapper.permissionIdList(userId);
            List<Permission> permissions = permissionService.listByIds(permissionIdList);
            String permissionCodes = permissions.stream().map(Permission::getCode).collect(Collectors.joining(","));

            authority = roleCodes.concat(",").concat(permissionCodes);

            redisUtil.set(authorityKey, authority, 60 * 60);
        }

        return authority;
    }
}

package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.UserRole;
import com.zhengjianting.autosoftware.mapper.UserRoleMapper;
import com.zhengjianting.autosoftware.service.UserRoleServiceI;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleServiceI {

}

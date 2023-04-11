package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.mapper.RoleMapper;
import com.zhengjianting.autosoftware.service.RoleServiceI;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> implements RoleServiceI {

}

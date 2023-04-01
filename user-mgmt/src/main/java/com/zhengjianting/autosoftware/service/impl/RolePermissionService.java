package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.RolePermission;
import com.zhengjianting.autosoftware.mapper.RolePermissionMapper;
import com.zhengjianting.autosoftware.service.RolePermissionI;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionI {

}

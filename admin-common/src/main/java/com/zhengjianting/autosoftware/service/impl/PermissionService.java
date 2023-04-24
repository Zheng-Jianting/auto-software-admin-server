package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.Permission;
import com.zhengjianting.autosoftware.mapper.PermissionMapper;
import com.zhengjianting.autosoftware.service.PermissionServiceI;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> implements PermissionServiceI {

}

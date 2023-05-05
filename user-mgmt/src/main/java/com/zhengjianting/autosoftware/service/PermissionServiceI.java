package com.zhengjianting.autosoftware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengjianting.autosoftware.entity.Permission;
import com.zhengjianting.autosoftware.util.FlatPermissionTree;
import com.zhengjianting.autosoftware.util.PermissionTree;

import java.util.List;

public interface PermissionServiceI extends IService<Permission> {
    PermissionTree getPermissionTree();
    FlatPermissionTree getFlatPermissionTree();
    List<Long> getNonLeafPermissionIdList();
}

package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.Permission;
import com.zhengjianting.autosoftware.mapper.PermissionMapper;
import com.zhengjianting.autosoftware.service.PermissionServiceI;
import com.zhengjianting.autosoftware.util.FlatPermissionTree;
import com.zhengjianting.autosoftware.util.PermissionTree;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> implements PermissionServiceI {
    private Permission root;
    private List<Permission> permissions;

    private void initPermissionTreeMetaData() {
        root = new Permission(0L, null, "all-permission", "权限树根节点", null, "N", null, null, 1L, "published", -1L, LocalDateTime.now(), -1L, LocalDateTime.now(), "N");
        permissions = list(new QueryWrapper<Permission>().eq("status", "published").eq("delete_flag", "N"));
    }

    @Override
    public PermissionTree getPermissionTree() {
        initPermissionTreeMetaData();
        return PermissionTree.buildPermissionTree(root, permissions);
    }

    @Override
    public FlatPermissionTree getFlatPermissionTree() {
        initPermissionTreeMetaData();
        return FlatPermissionTree.buildPermissionFlatTree(root, permissions);
    }
}

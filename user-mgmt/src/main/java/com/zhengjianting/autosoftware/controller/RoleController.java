package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.entity.RolePermission;
import com.zhengjianting.autosoftware.entity.UserRole;
import com.zhengjianting.autosoftware.service.impl.PermissionService;
import com.zhengjianting.autosoftware.service.impl.RolePermissionService;
import com.zhengjianting.autosoftware.service.impl.RoleService;
import com.zhengjianting.autosoftware.service.impl.UserRoleService;
import com.zhengjianting.autosoftware.util.FlatPermissionTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class RoleController {
    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RolePermissionService rolePermissionService;

    @GetMapping("/api/role/create")
    public Result getCreateRoleDialogInfo() {
        FlatPermissionTree flatPermissionTree = permissionService.getFlatPermissionTree();
        List<Long> expandedKeys = permissionService.getNonLeafPermissionIdList();

        JSONObject data = new JSONObject();
        data.set("flatPermissionTree", flatPermissionTree);
        data.set("expandedKeys", expandedKeys);

        return new Result(200, "get create role dialog info successfully", data);
    }

    @PostMapping("/api/role/create")
    public Result createRole(@RequestBody JSONObject params) {
        if (!params.containsKey("role") || !params.containsKey("checkedKeys")) {
            return new Result(400, "role and checkedKeys parameter is necessary");
        }

        Role role = params.getBean("role", Role.class);
        roleService.save(role);

        List<Long> permissionIdList = params.getJSONArray("checkedKeys").toList(Long.class);
        List<RolePermission> rolePermissions = permissionIdList.stream().map(i -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(i);
            return rolePermission;
        }).collect(Collectors.toList());
        rolePermissionService.saveBatch(rolePermissions);

        return new Result(200, "create role successfully");
    }

    @GetMapping("/api/role")
    public Result pageRole(@RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<Role> rolePage = roleService.page(new Page<>(pageIndex, pageSize), new QueryWrapper<Role>().eq("delete_flag", "N").orderByAsc("id"));
        List<JSONObject> roles = rolePage.getRecords().stream().map(role -> {
            JSONObject obj = new JSONObject();
            obj.set("id", role.getId());
            obj.set("roleCode", role.getRoleCode());
            obj.set("roleName", role.getRoleName());
            obj.set("description", role.getDescription());
            obj.set("creationDate", LocalDateTimeUtil.format(role.getCreationDate(), DatePattern.NORM_DATETIME_FORMATTER));
            obj.set("lastUpdateDate", LocalDateTimeUtil.format(role.getLastUpdateDate(), DatePattern.NORM_DATETIME_FORMATTER));
            return obj;
        }).collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.set("total", rolePage.getTotal());
        data.set("pageIndex", rolePage.getCurrent());
        data.set("pageSize", rolePage.getSize());
        data.set("pageCount", rolePage.getPages());
        data.set("roles", roles);

        return new Result(200, "page role successfully", data);
    }

    @GetMapping("/api/role/{id}")
    public Result getEditRoleDialogInfo(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        FlatPermissionTree flatPermissionTree = permissionService.getFlatPermissionTree();

        List<Long> expandedKeys = permissionService.getNonLeafPermissionIdList();

        List<RolePermission> checkedRolePermissions = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("delete_flag", "N").eq("role_id", id).orderByAsc("id"));
        List<Long> checkedKeys = checkedRolePermissions.stream().mapToLong(RolePermission::getPermissionId).boxed().collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.set("role", role);
        data.set("flatPermissionTree", flatPermissionTree);
        data.set("expandedKeys", expandedKeys);
        data.set("checkedKeys", checkedKeys);

        return new Result(200, "get edit role dialog info successfully", data);
    }

    @PostMapping("/api/role")
    public Result editRoleAndPermission(@RequestBody JSONObject params) {
        if (!params.containsKey("role") || !params.containsKey("checkedKeys")) {
            return new Result(400, "role and checkedKeys parameter is necessary");
        }

        JSONObject role = params.getJSONObject("role");
        List<Long> permissionIdList = params.getJSONArray("checkedKeys").toList(Long.class);
        List<RolePermission> rolePermissions = permissionIdList.stream().map(i -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getLong("id"));
            rolePermission.setPermissionId(i);
            return rolePermission;
        }).collect(Collectors.toList());

        roleService.update(new UpdateWrapper<Role>().eq("id", role.getLong("id")).set("role_code", role.getStr("roleCode")).set("role_name", role.getStr("roleName")).set("description", role.getStr("description")).set("last_update_date", LocalDateTime.now()));
        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", role.getLong("id")));
        rolePermissionService.saveBatch(rolePermissions);

        return new Result(200, "edit role and permission successfully");
    }

    @DeleteMapping("/api/role/{id}")
    public Result removeRole(@PathVariable("id") Long id) {
        roleService.update(new UpdateWrapper<Role>().eq("id", id).set("last_update_date", LocalDateTime.now()).set("delete_flag", "Y"));
        userRoleService.update(new UpdateWrapper<UserRole>().eq("role_id", id).set("last_update_date", LocalDateTime.now()).set("delete_flag", "Y"));
        rolePermissionService.update(new UpdateWrapper<RolePermission>().eq("role_id", id).set("last_update_date", LocalDateTime.now()).set("delete_flag", "Y"));
        return new Result(200, "remove role which id = " + id + " successfully");
    }
}

package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.entity.UserRole;
import com.zhengjianting.autosoftware.service.impl.RoleService;
import com.zhengjianting.autosoftware.service.impl.UserRoleService;
import com.zhengjianting.autosoftware.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    private JSONObject getResultDataFromPage(Page<User> userPage) {
        List<JSONObject> users = userPage.getRecords().stream().map(user -> {
            JSONObject obj = new JSONObject();
            obj.set("id", user.getId());
            obj.set("username", user.getUsername());
            obj.set("email", user.getEmail());
            obj.set("status", user.getStatus());
            obj.set("creationDate", LocalDateTimeUtil.format(user.getCreationDate(), DatePattern.NORM_DATETIME_FORMATTER));
            obj.set("lastUpdateDate", LocalDateTimeUtil.format(user.getLastUpdateDate(), DatePattern.NORM_DATETIME_FORMATTER));

            List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("delete_flag", "N").eq("user_id", user.getId()));
            userRoles.stream().map(userRole -> roleService.getById(userRole.getRoleId()).getRoleName()).reduce((acc, roleName) -> acc + ", " + roleName).ifPresent(roleNameList -> obj.set("roleName", roleNameList));
            return obj;
        }).collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.set("total", userPage.getTotal());
        data.set("pageIndex", userPage.getCurrent());
        data.set("pageSize", userPage.getSize());
        data.set("pageCount", userPage.getPages());
        data.set("users", users);

        return data;
    }

    @GetMapping("/api/user")
    public Result pageUser(@RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<User> userPage = userService.page(new Page<>(pageIndex, pageSize), new QueryWrapper<User>().eq("delete_flag", "N").orderByAsc("id"));
        return new Result(200, "page user successfully", getResultDataFromPage(userPage));
    }

    @GetMapping("/api/user/{name}")
    public Result findUserByName(@PathVariable("name") String username, @RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<User> userPage = userService.page(new Page<>(pageIndex, pageSize), new QueryWrapper<User>().eq("delete_flag", "N").like("username", username).orderByAsc("id"));
        return new Result(200, "find user by name successfully", getResultDataFromPage(userPage));
    }

    @PutMapping("/api/user")
    public Result updateUser(@RequestBody JSONObject user) {
        if (userService.update(new UpdateWrapper<User>()
                .eq("id", user.getLong("id"))
                .set("username", user.getStr("username"))
                .set("email", user.getStr("email"))
                .set("status", user.getStr("status"))
                .set("last_update_date", LocalDateTime.now()))
        ) {
            return new Result(200, "update user successfully");
        }
        return new Result(400, "update user failed");
    }

    @DeleteMapping("/api/user/{id}")
    public Result removeUser(@PathVariable("id") Long id) {
        if (userService.update(new UpdateWrapper<User>().eq("id", id).set("delete_flag", "Y"))) {
            return new Result(200, "remove user which id = " + id + " successfully");
        }
        return new Result(400, "remove user which id = " + id + " failed");
    }

    @GetMapping("/api/user/assign-role")
    public Result listAssignRole(@RequestParam("id") Long userId) {
        List<Long> ownedRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("delete_flag", "N").eq("user_id", userId)).stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<Role> roles = roleService.list(new QueryWrapper<Role>().eq("delete_flag", "N").orderByAsc("id"));
        List<JSONObject> data = roles.stream().map(role -> {
            JSONObject obj = new JSONObject();
            obj.set("roleId", role.getId());
            obj.set("roleName", role.getRoleName());
            obj.set("description", role.getDescription());
            obj.set("owned", ownedRoles.contains(role.getId()));
            return obj;
        }).collect(Collectors.toList());
        return new Result(200, "list assign role successfully", data);
    }
}

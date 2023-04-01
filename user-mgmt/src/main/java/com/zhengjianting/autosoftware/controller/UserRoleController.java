package com.zhengjianting.autosoftware.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.UserRole;
import com.zhengjianting.autosoftware.service.impl.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserRoleController {
    @Resource
    private UserRoleService userRoleService;

    @PostMapping("/api/user-role/assign-role")
    public Result assignRole(@RequestBody JSONObject params) {
        if (!params.containsKey("userId") || !params.containsKey("roleIdList")) {
            return new Result(400, "userId and roleIdList parameter is necessary");
        }
        Long userId = params.getLong("userId");
        List<Long> roleIdList = params.getJSONArray("roleIdList").toList(Long.class);

        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));

        List<UserRole> userRoleList = roleIdList.stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());

        if (userRoleService.saveBatch(userRoleList)) {
            return new Result(200, "assign role successfully");
        }
        return new Result(400, "assign role failed");
    }
}

package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhengjianting.autosoftware.common.lang.Result;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.service.impl.RoleService;
import com.zhengjianting.autosoftware.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;

@Slf4j
@RestController
public class OtherController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    // 保存用户的使用记录信息
    @PostMapping("/usageRecord/save")
    public Result saveUsageRecord(@RequestBody JSONObject data) {
        log.info("用户使用记录: " + data);
        return Result.success("");
    }

    // 获取当前登录的用户信息
    @GetMapping("/user/loginUser")
    public Result getLoginUser(Principal principal) {
        User user = userService.getByUsername(principal.getName());
        Assert.notNull(user, "查询当前登录用户：" + principal.getName() + "的信息失败");

        // 获取与该用户关联的角色
        JSONObject data = JSONUtil.parseObj(user);
        data.set("userRoles", roleService.list(new QueryWrapper<Role>().eq("delete_flag", "N").inSql("id", "select role_id from user_mgmt.user_role_t where delete_flag = 'N' and user_id = " + user.getId())));

        log.info("查询当前登录用户：" + principal.getName() + "的信息成功");
        return Result.success(data);
    }
}

package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhengjianting.autosoftware.common.dto.UserRecordDto;
import com.zhengjianting.autosoftware.common.lang.Const;
import com.zhengjianting.autosoftware.common.lang.Result;
import com.zhengjianting.autosoftware.entity.Project;
import com.zhengjianting.autosoftware.entity.Role;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.entity.UserRole;
import com.zhengjianting.autosoftware.repository.ProjectRepository;
import com.zhengjianting.autosoftware.service.impl.RoleService;
import com.zhengjianting.autosoftware.service.impl.UserRoleService;
import com.zhengjianting.autosoftware.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RoleService roleService;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private ProjectRepository projectRepository;

    // 普通用户注册用户信息
    @Transactional
    @PostMapping("/saveNormal")
    public Result saveNormalUser(@Validated @RequestBody User user) {
        User oldUser = userService.getByUsername(user.getUsername());
        if (oldUser != null) {
            log.error("普通用户注册失败，用户：" + user.getUsername() + "已经存在");
            return Result.fail("注册失败！用户：" + user.getUsername() + "已经存在，请重试！");
        }

        user.setPassword(StringUtils.isEmpty(user.getPassword()) ? bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD) : bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAvatar(StringUtils.isEmpty(user.getAvatar()) ? Const.DEFAULT_AVATAR : user.getAvatar());
        user.setStatus(Const.ENABLED_STATUS);
        userService.save(user);

        // 默认具有普通用户角色
        User newUser = userService.getByUsername(user.getUsername());
        Role role = roleService.getOne(new QueryWrapper<Role>().eq("delete_flag", "N").eq("role_code", "user"));
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(role.getId());
        userRoleService.save(userRole);

        log.info("普通用户：" + user.getUsername() + "注册成功");
        return Result.success(user);
    }

    // 普通用户更新用户信息
    @PostMapping("/updateNormal")
    public Result updateNormalUser(@Validated @RequestBody User user) {
        log.info("普通用户：" + user.getUsername() + "更新信息成功");
        return userService.updateUser(user);
    }

    // 普通用户注销账号
    @PostMapping("/userDelete/{userId}")
    @Transactional
    public Result userDelete(@PathVariable(name = "userId") Long userId) {
        userService.removeById(userId);
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
        log.info("普通用户：" + userId + "注销账号成功");
        return Result.success("");
    }

    // 新建项目
    @PostMapping("/createUserProject")
    public Result createUserProject(@RequestBody Project project) {
        project.setCreated(DateUtil.now());
        project.setUpdated(DateUtil.now());
        project.setStatus(1);
        projectRepository.insert(project);
        log.info("用户：" + project.getUserId() + "新建分析项目：" + project.getProjectName() + "成功");
        return Result.success(project);
    }

    private List<JSONObject> getRecordsByModule(Project project, String module) {
        List<JSONObject> records = null;
        switch (module) {
            case "mindMap":
                records = project.getMindMap();
                break;
            case "usercaseDiagram":
                records = project.getUsercaseDiagram();
                break;
            case "activityDiagram":
                records = project.getActivityDiagram();
                break;
            case "erDiagram":
                records = project.getErDiagram();
                break;
            case "uiDiagram":
                records = project.getUiDiagram();
                break;
            case "autoCode":
                records = project.getAutoCode();
                break;
        }
        return records;
    }

    // 保存记录
    @PostMapping("/saveRecord")
    public Result saveRecord(@RequestBody UserRecordDto userRecordDto) {
        userRecordDto.setUpdated(DateUtil.now());
        JSONObject newUserRecord = JSONUtil.parseObj(userRecordDto, false);

        Project project = projectRepository.findById(userRecordDto.getKey()).orElse(null);
        if (project != null) {
            List<JSONObject> records = getRecordsByModule(project, userRecordDto.getModule());
            if (records != null) {
                records.removeIf(k -> k.get("recordId").equals(newUserRecord.get("recordId")));
                records.add(0, newUserRecord);
            }

            project.setUpdated(DateUtil.now());
            projectRepository.save(project);
            log.info("用户：" + project.getUserId() + "保存分析记录：" + project.getProjectName() + "成功");

            return Result.success(userRecordDto);
        } else {
            log.error("用户保存分析记录失败，该项目不存在");
            return Result.fail("该项目不存在！");
        }
    }

    @PostMapping("/deleteUserProject/{id}")
    public Result deleteUserProject(@PathVariable String id) {
        projectRepository.deleteById(id);
        log.info("用户删除分析项目：" + id + "成功");
        return Result.success("删除项目成功！");
    }

    // 获取用户的项目
    @GetMapping("/getUserProject/{id}")
    public Result getUserProject(@PathVariable(name = "id") String userId) {
        List<Project> userProjects = projectRepository.findByUserId(userId);
        userProjects.sort((p1, p2) -> p2.getUpdated().compareTo(p1.getUpdated()));
        log.info("查询用户：" + userId + "的所有分析项目");
        return Result.success(userProjects);
    }

    // 删除记录
    @PostMapping("/deleteDiagramRecord")
    public Result deleteDiagramRecord(@RequestBody UserRecordDto userRecordDto) {
        JSONObject deleteRecord = JSONUtil.createObj().putOnce("recordId", userRecordDto.getRecordId());

        Project project = projectRepository.findById(userRecordDto.getKey()).orElse(null);
        if (project != null) {
            List<JSONObject> records = getRecordsByModule(project, userRecordDto.getModule());
            if (records != null) {
                records.removeIf(k -> k.get("recordId").equals(deleteRecord.get("recordId")));
            }

            project.setUpdated(DateUtil.now());
            projectRepository.save(project);
            log.info("用户：" + project.getUserId() + "删除分析记录" + project.getProjectName() + "成功");

            return Result.success(userRecordDto);
        } else {
            log.error("用户删除分析记录失败，该项目不存在");
            return Result.fail("该项目不存在！");
        }
    }

    // 删除某个项目某个模块的所有记录
    @PostMapping("/deleteModuleRecord")
    public Result deleteModuleRecord(@RequestBody UserRecordDto userRecordDto){
        Project project = projectRepository.findById(userRecordDto.getKey()).orElse(null);
        if (project != null) {
            List<JSONObject> emptyModule = new ArrayList<>();
            switch (userRecordDto.getModule()) {
                case "mindMap":
                    project.setMindMap(emptyModule);
                    break;
                case "usercaseDiagram":
                    project.setUsercaseDiagram(emptyModule);
                    break;
                case "activityDiagram":
                    project.setActivityDiagram(emptyModule);
                    break;
                case "erDiagram":
                    project.setErDiagram(emptyModule);
                    break;
                case "uiDiagram":
                    project.setUiDiagram(emptyModule);
                    break;
                case "autoCode":
                    project.setAutoCode(emptyModule);
                    break;
            }

            project.setUpdated(DateUtil.now());
            projectRepository.save(project);
            log.info("用户：" + project.getUserId() + "删除分析记录" + project.getProjectName() + "的模块记录成功");

            return Result.success(userRecordDto);
        } else {
            log.error("用户删除该模块所有记录失败，该项目不存在");
            return Result.fail("该项目不存在！");
        }
    }
}

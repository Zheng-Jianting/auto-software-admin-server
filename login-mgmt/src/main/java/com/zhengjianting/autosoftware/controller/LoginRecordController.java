package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.LoginRecord;
import com.zhengjianting.autosoftware.service.impl.LoginRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class LoginRecordController {
    @Resource
    private LoginRecordService loginRecordService;

    @PostMapping("/api/login-record")
    public Result saveLoginRecord(@RequestBody JSONObject params) {
        if (!params.containsKey("username") || !params.containsKey("ipAddress")) {
            return new Result(400, "username and ipAddress parameter is necessary");
        }

        String username = params.getStr("username");
        String ipAddress = params.getStr("ipAddress");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ip", ipAddress);
        paramMap.put("output", "json");
        paramMap.put("key", "your key");
        JSONObject responseBody = JSONUtil.parseObj(HttpUtil.get("https://restapi.amap.com/v3/ip", paramMap));

        String ipProvince = NetUtil.isInnerIP(ipAddress) ? "局域网" : ObjectUtil.isEmpty(responseBody.get("province")) ? "国外" : responseBody.getStr("province");
        String ipCity = NetUtil.isInnerIP(ipAddress) ? "局域网" : ObjectUtil.isEmpty(responseBody.get("city")) ? "国外" : responseBody.getStr("city");

        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUsername(username);
        loginRecord.setIpAddress(ipAddress);
        loginRecord.setIpProvince(ipProvince);
        loginRecord.setIpCity(ipCity);
        loginRecord.setLoginDate(LocalDateTime.now());

        if (loginRecordService.save(loginRecord)) {
            return new Result(200, "save login record: " + params + " successfully");
        }
        return new Result(400, "save login record: " + params + " failed");
    }

    private JSONObject getResultDataFromPage(Page<LoginRecord> loginRecordPage) {
        List<JSONObject> loginRecords = loginRecordPage.getRecords().stream().map(loginRecord -> {
            JSONObject obj = new JSONObject(loginRecord);
            obj.computeIfPresent("loginDate", (key, value) -> LocalDateTimeUtil.format(loginRecord.getLoginDate(), DatePattern.NORM_DATETIME_FORMATTER));
            return obj;
        }).collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.set("total", loginRecordPage.getTotal());
        data.set("pageIndex", loginRecordPage.getCurrent());
        data.set("pageSize", loginRecordPage.getSize());
        data.set("pageCount", loginRecordPage.getPages());
        data.set("loginRecords", loginRecords);

        return data;
    }

    @GetMapping("/api/login-record")
    public Result pageLoginRecord(@RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<LoginRecord> loginRecordPage = loginRecordService.page(new Page<>(pageIndex, pageSize), new QueryWrapper<LoginRecord>().eq("delete_flag", "N").orderByDesc("login_date"));
        return new Result(200, "page login record successfully", getResultDataFromPage(loginRecordPage));
    }

    @GetMapping("/api/login-record/{name}")
    public Result findLoginRecordByName(@PathVariable("name") String username, @RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<LoginRecord> loginRecordPage = loginRecordService.page(new Page<>(pageIndex, pageSize), new QueryWrapper<LoginRecord>().eq("delete_flag", "N").like("username", username).orderByDesc("login_date"));
        return new Result(200, "find login record by name successfully", getResultDataFromPage(loginRecordPage));
    }

    @DeleteMapping("/api/login-record/{id}")
    public Result removeLoginRecord(@PathVariable("id") Long id) {
        if (loginRecordService.update(new UpdateWrapper<LoginRecord>().eq("id", id).set("delete_flag", "Y"))) {
            return new Result(200, "remove login record which id = " + id + " successfully");
        }
        return new Result(400, "remove login record which id = " + id + " failed");
    }
}

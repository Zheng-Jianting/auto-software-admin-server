package com.zhengjianting.autosoftware.service.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.LoginRecord;
import com.zhengjianting.autosoftware.mapper.LoginRecordMapper;
import com.zhengjianting.autosoftware.service.LoginRecordServiceI;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginRecordService extends ServiceImpl<LoginRecordMapper, LoginRecord> implements LoginRecordServiceI {
    @Override
    public void saveLoginRecord(String username, String ipAddress) {
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

        this.save(loginRecord);
    }
}

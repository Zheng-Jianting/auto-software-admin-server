package com.zhengjianting.autosoftware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengjianting.autosoftware.entity.LoginRecord;

public interface LoginRecordServiceI extends IService<LoginRecord> {
    void saveLoginRecord(String username, String ipAddress);
}

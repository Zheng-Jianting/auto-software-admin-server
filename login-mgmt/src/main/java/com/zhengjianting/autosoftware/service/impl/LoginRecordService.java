package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.LoginRecord;
import com.zhengjianting.autosoftware.mapper.LoginRecordMapper;
import com.zhengjianting.autosoftware.service.LoginRecordServiceI;
import org.springframework.stereotype.Service;

@Service
public class LoginRecordService extends ServiceImpl<LoginRecordMapper, LoginRecord> implements LoginRecordServiceI {

}

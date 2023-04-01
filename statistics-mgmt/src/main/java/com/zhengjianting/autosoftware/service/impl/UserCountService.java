package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.UserCount;
import com.zhengjianting.autosoftware.mapper.UserCountMapper;
import com.zhengjianting.autosoftware.service.UserCountServiceI;
import org.springframework.stereotype.Service;

@Service
public class UserCountService extends ServiceImpl<UserCountMapper, UserCount> implements UserCountServiceI {

}

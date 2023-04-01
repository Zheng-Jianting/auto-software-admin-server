package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.mapper.UserMapper;
import com.zhengjianting.autosoftware.service.UserServiceI;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> implements UserServiceI {

}

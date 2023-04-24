package com.zhengjianting.autosoftware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengjianting.autosoftware.entity.User;

public interface UserServiceI extends IService<User> {
    User getByUsername(String username);
    String getUserAuthorityInfo(Long userId);
}

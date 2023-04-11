package com.zhengjianting.autosoftware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengjianting.autosoftware.common.lang.Result;
import com.zhengjianting.autosoftware.entity.User;

public interface UserServiceI extends IService<User> {
    User getByUsername(String username);
    Result updateUser(User user);
    String getUserAuthorityInfo(Long userId);
}

package com.zhengjianting.autosoftware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhengjianting.autosoftware.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<Long> permissionIdList(Long userId);
}

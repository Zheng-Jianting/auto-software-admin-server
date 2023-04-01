package com.zhengjianting.autosoftware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhengjianting.autosoftware.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}

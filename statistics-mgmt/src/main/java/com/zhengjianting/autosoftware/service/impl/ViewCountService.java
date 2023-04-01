package com.zhengjianting.autosoftware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengjianting.autosoftware.entity.ViewCount;
import com.zhengjianting.autosoftware.mapper.ViewCountMapper;
import com.zhengjianting.autosoftware.service.ViewCountServiceI;
import org.springframework.stereotype.Service;

@Service
public class ViewCountService extends ServiceImpl<ViewCountMapper, ViewCount> implements ViewCountServiceI {

}

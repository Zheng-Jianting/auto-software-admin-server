package com.zhengjianting.autosoftware.common.dto;

import cn.hutool.json.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一个项目有多个模块，每个模块存在多条记录
 * 接受前端保存某个项目的记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecordDto {
    private String key;           // 项目key
    private String userId;        // 用户id
    private String recordId;      // 记录id
    private String module;        // 属于哪个部分
    private String recordName;    // 记录名字
    private String text;          // 需求文本
    private JSONArray recordData; // 记录数据
    private String updated;       // 更新时间
}

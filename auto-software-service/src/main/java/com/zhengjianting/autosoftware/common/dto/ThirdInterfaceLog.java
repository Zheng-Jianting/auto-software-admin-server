package com.zhengjianting.autosoftware.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录第三方接口日志信息结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdInterfaceLog {
    private String logLevel; // 日志等级
    private String message;  // 日志信息
}

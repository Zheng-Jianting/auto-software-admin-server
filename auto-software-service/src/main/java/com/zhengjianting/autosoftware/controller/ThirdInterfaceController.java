package com.zhengjianting.autosoftware.controller;

import com.zhengjianting.autosoftware.common.dto.ThirdInterfaceLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/thirdInterface")
public class ThirdInterfaceController {

    // 记录前端访问第三方接口日志信息
    @PostMapping("/log")
    public void log(@RequestBody ThirdInterfaceLog thirdInterfaceLog) {
        try {
            if (thirdInterfaceLog.getLogLevel().equals("info")) {
                log.info(thirdInterfaceLog.getMessage());
            } else if(thirdInterfaceLog.getLogLevel().equals("error")) {
                log.error(thirdInterfaceLog.getMessage());
            }
        } catch (RuntimeException e ) {
            log.error("访问第三方接口出现错误");
        }
    }
}

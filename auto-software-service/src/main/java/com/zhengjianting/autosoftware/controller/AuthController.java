package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.map.MapUtil;
import com.zhengjianting.autosoftware.common.lang.Const;
import com.zhengjianting.autosoftware.common.lang.Result;
import com.zhengjianting.autosoftware.util.CaptchaUtil;
import com.zhengjianting.autosoftware.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class AuthController {

    @Resource
    private RedisUtil redisUtil;

    // 图片验证码
    @GetMapping("/captchaImage")
    public Result getCaptchaImage() throws IOException {
        Object[] objs = CaptchaUtil.newBuilder()
                .setWidth(100)   // 设置图片的宽度
                .setHeight(30)   // 设置图片的高度
                .setSize(5)      // 设置字符的个数
                .setLines(3)     // 设置干扰线的条数
                .setFontSize(20) // 设置字体的大小
                .setTilt(false)  // 设置是否需要倾斜
                .setBackgroundColor(Color.lightGray) // 设置验证码的背景颜色
                .build()
                .createImage();

        String captchaKey = UUID.randomUUID().toString();
        String captcha = (String) objs[0];
        log.info("获取验证码：" + captcha);

        BufferedImage image = (BufferedImage) objs[1];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Img = "data:image/png;base64," + encoder.encode(outputStream.toByteArray());

        redisUtil.hset(Const.CAPTCHA_KEY, captchaKey, captcha, 120);

        return Result.success(MapUtil.builder().put("captchaKey", captchaKey).put("captchaImage", base64Img).build());
    }
}

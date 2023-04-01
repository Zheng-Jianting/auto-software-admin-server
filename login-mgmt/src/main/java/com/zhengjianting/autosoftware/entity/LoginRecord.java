package com.zhengjianting.autosoftware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("login_record_t")
public class LoginRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String ipAddress;
    private String ipProvince;
    private String ipCity;
    private LocalDateTime loginDate;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private String deleteFlag;
}

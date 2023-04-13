package com.zhengjianting.autosoftware.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto implements Serializable {
    @NotBlank(message = "旧密码不能为空！")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空！")
    private String newPassword;
}

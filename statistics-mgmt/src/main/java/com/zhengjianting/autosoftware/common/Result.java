package com.zhengjianting.autosoftware.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public Result(Integer code, String message) {
        this(code, message, null);
    }
}

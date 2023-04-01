package com.zhengjianting.autosoftware.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCount implements Serializable {
    private Long count;
    private String month;
}

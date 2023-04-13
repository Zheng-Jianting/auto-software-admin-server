package com.zhengjianting.autosoftware.entity;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_project")
public class Project implements Serializable {
    @Id
    private String key;
    private String userId;
    private String projectName;
    private String fromModule;
    /**
     * 保存的数据记录
     * 数组的每个记录字段
     *     {
     *          recordId: "",
     *          recordData: [cells]
     *     }
     */
    private List<JSONObject> mindMap;
    private List<JSONObject> usercaseDiagram;
    private List<JSONObject> activityDiagram;
    private List<JSONObject> erDiagram;
    private List<JSONObject> uiDiagram;
    private List<JSONObject> autoCode;
    private String created;
    private String updated;
    @Field("statu")
    private Integer status;
}

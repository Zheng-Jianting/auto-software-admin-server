package com.zhengjianting.autosoftware.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhengjianting.autosoftware.common.Result;
import com.zhengjianting.autosoftware.entity.Project;
import com.zhengjianting.autosoftware.entity.User;
import com.zhengjianting.autosoftware.repository.ProjectRepository;
import com.zhengjianting.autosoftware.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ProjectController {
    @Resource
    private ProjectRepository projectRepository;

    @Resource
    private UserService userService;

    private JSONObject getResultDataFromPage(Page<Project> projectPage) {
        JSONObject data = new JSONObject();
        data.set("total", projectPage.getTotalElements());
        data.set("pageIndex", projectPage.getNumber() + 1);
        data.set("pageSize", projectPage.getSize());
        data.set("pageCount", projectPage.getTotalPages());

        List<JSONObject> projects = projectPage.get().map(JSONObject::new).collect(Collectors.toList());
        projects.forEach(project -> {
            User user = userService.getById(project.getLong("userId"));
            project.set("username", user.getUsername());
        });
        data.set("projects", projects);

        return data;
    }

    @GetMapping("/api/project")
    public Result pageProject(@RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<Project> projectPage = projectRepository.findAll(PageRequest.of(pageIndex - 1, pageSize).withSort(Sort.by(Sort.Direction.DESC, "updated")));
        return new Result(200, "page project successfully", getResultDataFromPage(projectPage));
    }

    @GetMapping("/api/project/{name}")
    public Result findProjectByName(@PathVariable("name") String name, @RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Page<Project> projectPage = projectRepository.findByProjectNameLikeIgnoreCase(name, PageRequest.of(pageIndex - 1, pageSize).withSort(Sort.by(Sort.Direction.DESC, "updated")));
        return new Result(200, "find project by name successfully", getResultDataFromPage(projectPage));
    }

    @PutMapping("/api/project")
    public Result updateProjectBasicInfo(@RequestBody Project newProject) {
        Project project = projectRepository.findById(newProject.getId()).orElse(null);
        if (project == null) {
            return new Result(400, "can not find project by id = " + newProject.getId());
        }
        project.setProjectName(newProject.getProjectName());
        project.setStatus(newProject.getStatus());
        project.setUpdated(DateUtil.now());
        projectRepository.save(project);
        return new Result(200, "update project which id = " + project.getId() + " successfully");
    }

    @GetMapping("/api/project/{id}/download")
    public ResponseEntity<byte[]> downloadProject(@PathVariable("id") String id) {
        Project project = projectRepository.findById(id).orElse(null);
        byte[] bytes = JSONUtil.parseObj(project, false).toStringPretty().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", Objects.requireNonNull(project).getProjectName()) + ".txt");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(bytes);
    }

    @DeleteMapping("/api/project/{id}")
    public Result deleteProject(@PathVariable("id") String id) {
        projectRepository.deleteById(id);
        return new Result(200, "delete project which id = " + id + " successfully");
    }

    private List<JSONObject> getProjectRecordByModule(Project project, String module) {
        List<JSONObject> records = new ArrayList<>();
        switch (module) {
            case "mindMap":
                records = project.getMindMap();
                break;
            case "usercaseDiagram":
                records = project.getUsercaseDiagram();
                break;
            case "activityDiagram":
                records = project.getActivityDiagram();
                break;
            case "erDiagram":
                records = project.getErDiagram();
                break;
            case "uiDiagram":
                records = project.getUiDiagram();
                break;
            case "autoCode":
                records = project.getAutoCode();
                break;
        }
        return records;
    }

    @GetMapping("/api/project/{id}/record/{module}")
    public Result pageProjectRecord(@PathVariable("id") String id, @PathVariable("module") String module, @RequestParam("page-index") Integer pageIndex, @RequestParam("page-size") Integer pageSize) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return new Result(400, "can not find project by id = " + id);
        }

        List<JSONObject> records = getProjectRecordByModule(project, module);
        records.forEach(record -> record.computeIfPresent("recordData", (key, value) -> JSONUtil.toJsonStr(value)));

        JSONObject data = new JSONObject();
        data.set("total", records.size());
        data.set("pageIndex", pageIndex);
        data.set("pageSize", pageSize);
        data.set("pageCount", records.size() / pageSize + 1);
        data.set("records", records.stream().skip((long) (pageIndex - 1) * pageSize).limit(pageSize).collect(Collectors.toList()));

        return new Result(200, "page project record successfully", data);
    }

    // return value: records whether exists record which id = recordId
    private boolean removeRecordById(List<JSONObject> records, String recordId) {
        boolean exist = false;
        Iterator<JSONObject> iterator = records.iterator();
        while (iterator.hasNext()) {
            JSONObject record = iterator.next();
            if (record.getStr("recordId").equals(recordId)) {
                exist = true;
                iterator.remove();
                break;
            }
        }
        return exist;
    }

    @PutMapping("/api/project/record")
    public Result updateProjectRecord(@RequestBody JSONObject newRecord) {
        String projectId = newRecord.getStr("key");
        String module = newRecord.getStr("module");
        String recordId = newRecord.getStr("recordId");

        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return new Result(400, "can not find project by id = " + projectId);
        }

        List<JSONObject> records = getProjectRecordByModule(project, module);
        if (removeRecordById(records, recordId)) {
            newRecord.computeIfPresent("recordData", (key, value) -> JSONUtil.parseArray(value));
            newRecord.set("updated", DateUtil.now());
            records.add(0, newRecord);
            project.setUpdated(DateUtil.now());
            projectRepository.save(project);
            return new Result(200, "update project record successfully");
        }

        return new Result(400, "record do not exist");
    }

    @DeleteMapping("/api/project/{project-id}/record/{module}/{record-id}")
    public Result deleteProjectRecord(@PathVariable("project-id") String projectId, @PathVariable("module") String module, @PathVariable("record-id") String recordId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return new Result(400, "can not find project by id = " + projectId);
        }

        List<JSONObject> records = getProjectRecordByModule(project, module);
        if (removeRecordById(records, recordId)) {
            project.setUpdated(DateUtil.now());
            projectRepository.save(project);
            return new Result(200, "delete project record successfully");
        }

        return new Result(400, "record do not exist");
    }
}

package com.zhengjianting.autosoftware.util;

import com.zhengjianting.autosoftware.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatPermissionTree {
    private Long id;
    private Long parentId;
    private String code;
    private String description;
    private String moduleType;
    private String isPermission;
    private String requestUrl;
    private String requestMethod;
    private Long sort;
    private String status;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private String deleteFlag;
    private List<FlatPermissionTree> children;

    public FlatPermissionTree(Permission permission, List<FlatPermissionTree> children) {
        this(
                permission.getId(),
                permission.getParentId(),
                permission.getCode(),
                permission.getDescription(),
                permission.getModuleType(),
                permission.getIsPermission(),
                permission.getRequestUrl(),
                permission.getRequestMethod(),
                permission.getSort(),
                permission.getStatus(),
                permission.getCreatedBy(),
                permission.getCreationDate(),
                permission.getLastUpdatedBy(),
                permission.getLastUpdateDate(),
                permission.getDeleteFlag(),
                children
        );
    }

    public static FlatPermissionTree buildPermissionFlatTree(Permission root, List<Permission> permissions) {
        if (root.getIsPermission().equals("Y")) {
            return new FlatPermissionTree(root, null);
        }

        boolean isRoot = false;
        for (Permission permission : permissions) {
            if (permission.getParentId() == null) {
                isRoot = true;
                break;
            }
        }

        FlatPermissionTree tree = new FlatPermissionTree(root, new ArrayList<>());
        List<Permission> subTreePermissionList = new ArrayList<>();
        Iterator<Permission> iterator = permissions.iterator();

        while (iterator.hasNext()) {
            Permission permission = iterator.next();
            if (isRoot && permission.getParentId() == null) {
                subTreePermissionList.add(permission);
                iterator.remove();
            } else if (permission.getParentId().equals(root.getId())) {
                subTreePermissionList.add(permission);
                iterator.remove();
            }
        }

        subTreePermissionList.sort(Comparator.comparingLong(Permission::getSort));

        for (Permission subTreePermission : subTreePermissionList) {
            FlatPermissionTree subTree = buildPermissionFlatTree(subTreePermission, permissions);
            tree.children.add(subTree);
        }

        return tree;
    }
}

package com.zhengjianting.autosoftware.util;

import com.zhengjianting.autosoftware.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTree {
    private Permission permission;
    private List<PermissionTree> children;

    public static PermissionTree buildPermissionTree(Permission root, List<Permission> permissions) {
        if (root.getIsPermission().equals("Y")) {
            return new PermissionTree(root, null);
        }

        boolean isRoot = false;
        for (Permission permission : permissions) {
            if (permission.getParentId() == null) {
                isRoot = true;
                break;
            }
        }

        PermissionTree tree = new PermissionTree(root, new ArrayList<>());
        List<Permission> subTreePermissionList = new ArrayList<>();
        Iterator<Permission> iterator = permissions.iterator();

        if (isRoot) {
            while (iterator.hasNext()) {
                Permission permission = iterator.next();
                if (permission.getParentId() == null) {
                    subTreePermissionList.add(permission);
                    iterator.remove();
                }
            }
        } else {
            while (iterator.hasNext()) {
                Permission permission = iterator.next();
                if (permission.getParentId().equals(root.getId())) {
                    subTreePermissionList.add(permission);
                    iterator.remove();
                }
            }
        }

        subTreePermissionList.sort(Comparator.comparingLong(Permission::getSort));

        for (Permission subTreePermission : subTreePermissionList) {
            PermissionTree subTree = buildPermissionTree(subTreePermission, permissions);
            tree.children.add(subTree);
        }

        return tree;
    }
}

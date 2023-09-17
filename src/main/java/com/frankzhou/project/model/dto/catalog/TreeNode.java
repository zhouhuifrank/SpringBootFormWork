package com.frankzhou.project.model.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreeNode {

    private Long id;

    private Long parentId;

    private String name;

    private List<TreeNode> childList;

    public void addChildNode(TreeNode node) {
        this.childList.add(node);
    }
}

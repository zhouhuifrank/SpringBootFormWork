package com.frankzhou.project.model.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long parentId;

    private String catalogName;

    private Integer level;

    private String treePath;

    private Integer sortNum;

    private List<CatalogDTO> childCatalogList;
}

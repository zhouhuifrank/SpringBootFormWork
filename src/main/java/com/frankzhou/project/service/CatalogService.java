package com.frankzhou.project.service;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.catalog.CatalogDTO;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 目录服务
 * @date 2023-09-10
 */
public interface CatalogService {

    ResultDTO<Boolean> addCatalogNode(CatalogDTO catalogDTO);

    ResultDTO<Boolean> updateCatalogNode(CatalogDTO catalogDTO);

    ResultDTO<Boolean> deleteCatalogNode(DeleteRequest deleteRequest);

    ResultDTO<CatalogDTO> getById(Long id);

    ResultDTO<CatalogDTO> getCatalogTree(String catalogName, Integer level);
}

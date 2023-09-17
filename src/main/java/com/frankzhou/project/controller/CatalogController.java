package com.frankzhou.project.controller;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.catalog.CatalogDTO;
import com.frankzhou.project.service.CatalogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-16
 */
@Slf4j
@Api(tags = "目录")
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Resource
    private CatalogService catalogService;

    @ApiOperation(value = "新增目录节点")
    @PostMapping("/add")
    public ResultDTO<Boolean> addCatalogNode(@RequestBody CatalogDTO catalogDTO) {
        return catalogService.addCatalogNode(catalogDTO);
    }

    @ApiOperation(value = "更新目录节点")
    @PostMapping("/update")
    public ResultDTO<Boolean> updateCatalogNode(@RequestBody CatalogDTO catalogDTO) {
        return catalogService.updateCatalogNode(catalogDTO);
    }

    @ApiOperation(value = "删除目录节点")
    @PostMapping("/delete")
    public ResultDTO<Boolean> deleteCatalogNode(@RequestBody DeleteRequest deleteRequest) {
        return catalogService.deleteCatalogNode(deleteRequest);
    }

    @ApiOperation(value = "获取单个目录节点")
    @GetMapping("/get/{id}")
    public ResultDTO<CatalogDTO> getOneCatalogNode(@PathVariable("id") Long id) {
        return catalogService.getById(id);
    }

    @ApiOperation(value = "获取目录树")
    @GetMapping("/tree")
    public ResultDTO<CatalogDTO> getCatalogTree(@RequestParam("catalogName") String catalogName, @RequestParam("level") Integer level) {
        return catalogService.getCatalogTree(catalogName,level);
    }

    @ApiOperation(value = "移动目录节点")
    @PostMapping("/move")
    public ResultDTO<Boolean> moveCatalog(@RequestParam("source") Long source,@RequestParam("target") Long target) {
        return null;
    }


}

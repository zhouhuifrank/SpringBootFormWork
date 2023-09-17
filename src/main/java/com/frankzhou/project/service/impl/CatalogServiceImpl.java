package com.frankzhou.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.ResultCodeConstant;
import com.frankzhou.project.common.ResultCodeDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.ListUtil;
import com.frankzhou.project.mapper.CatalogMapper;
import com.frankzhou.project.model.dto.catalog.CatalogDTO;
import com.frankzhou.project.model.entity.Catalog;
import com.frankzhou.project.service.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-10
 */
@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

    @Resource
    private CatalogMapper catalogMapper;

    @Override
    public ResultDTO<Boolean> addCatalogNode(CatalogDTO catalogDTO) {
        if (ObjectUtil.isNull(catalogDTO)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }
        String catalogName = catalogDTO.getCatalogName();
        Long parentId = catalogDTO.getParentId();
        if (StringUtils.isBlank(catalogName)) {
            throw new BusinessException(new ResultCodeDTO(91132,"catalog name is not null","目录名称不能为空"));
        }
        if (ObjectUtil.isNull(parentId)) {
            throw new BusinessException(new ResultCodeDTO(91132,"parent id is not null","父结点编号不能为空"));
        }

        // 查询父节点
        ResultDTO<CatalogDTO> result = getById(parentId);
        if (result.getResultCode() != HttpStatus.HTTP_OK) {
            throw new BusinessException(new ResultCodeDTO(91132,"parent node is not exist","父节点不存在"));
        }
        CatalogDTO parentNode = result.getData();
        Integer level = parentNode.getLevel();
        String treePath = parentNode.getTreePath();

        // 目录层级限制
        if (level >= 4) {
            throw new BusinessException(new ResultCodeDTO(91132,"tree level is exceed limit","目录层级不能超过4层"));
        }

        // 目录名称去重
        List<CatalogDTO> catalogDTOList = getByParentId(parentId);
        Integer maxSortNum = 0;
        if (CollectionUtil.isNotEmpty(catalogDTOList)) {
            for (CatalogDTO item : catalogDTOList) {
                if (item.getCatalogName().equals(catalogName)) {
                    throw new BusinessException(new ResultCodeDTO(91132,"catalog name is duplicate","同一层级,目录名称不可重复"));
                }

                maxSortNum = maxSortNum < item.getSortNum() ? item.getSortNum() : maxSortNum;
            }
        }

        // 属性赋值
        String currentTreePath = treePath + "/" + catalogName;
        catalogDTO.setTreePath(currentTreePath);
        catalogDTO.setLevel(level+1);
        catalogDTO.setSortNum(maxSortNum+1);

        Catalog insertData = new Catalog();
        BeanUtil.copyProperties(catalogDTO,insertData);
        Integer insertCount = catalogMapper.insert(insertData);
        if (insertCount < 1) {
            throw new BusinessException(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> updateCatalogNode(CatalogDTO catalogDTO) {
        if (ObjectUtil.isNull(catalogDTO)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }
        if (ObjectUtil.isNull(catalogDTO.getId())) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }
        if (StringUtils.isBlank(catalogDTO.getCatalogName())) {
            throw new BusinessException(new ResultCodeDTO(91132,"catalog name is null", "目录名称不能为空"));
        }

        ResultDTO<CatalogDTO> oneById = getById(catalogDTO.getId());
        if (oneById.getResultCode() != HttpStatus.HTTP_OK) {
            throw new BusinessException(ResultCodeConstant.DB_QUERY_NO_DATA);
        }
        // 重复性检验catalogName
        CatalogDTO oldNode = oneById.getData();
        Long parentId = oldNode.getParentId();
        List<CatalogDTO> catalogDTOList = getByParentId(parentId);
        if (CollectionUtil.isNotEmpty(catalogDTOList)) {
            String alterName = catalogDTO.getCatalogName();
            Set<String> nameSet = catalogDTOList.stream().map(CatalogDTO::getCatalogName).collect(Collectors.toSet());
            if (nameSet.contains(alterName) && !alterName.equals(oldNode.getCatalogName())) {
                throw new BusinessException(new ResultCodeDTO(91132,"same level name duplicate","同一层级目录名称不能重复"));
            }
        }

        // 修改子节点的path
        String treePath = oldNode.getTreePath();
        List<CatalogDTO> catalogByParentId = getByParentId(oldNode.getId());
        if (CollectionUtil.isNotEmpty(catalogByParentId)) {
            String oldName = oldNode.getCatalogName();
            String newName = catalogDTO.getCatalogName();
            for (CatalogDTO target : catalogByParentId) {
                String alterPath = target.getTreePath();
                target.setTreePath(alterPath.replace(oldName, newName));
            }

            List<Catalog> updateList = ListUtil.listConvert(catalogByParentId, Catalog.class);
            Integer count = catalogMapper.batchUpdate(updateList);
            if (!count.equals(updateList.size())) {
                throw new BusinessException(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
            }
        }

        // 修改当前节点
        String oldPath = oldNode.getTreePath();
        String newPath = oldPath.replace(oldNode.getCatalogName(), catalogDTO.getCatalogName());
        oldNode.setCatalogName(catalogDTO.getCatalogName());
        oldNode.setTreePath(newPath);
        Catalog updateData = new Catalog();
        BeanUtil.copyProperties(oldNode, updateData);
        Integer updateCount = catalogMapper.updateById(updateData);
        if (updateCount < 1) {
            throw new BusinessException(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> deleteCatalogNode(DeleteRequest deleteRequest) {
        if (ObjectUtil.isNull(deleteRequest)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 查询要删除的节点
        ResultDTO<CatalogDTO> oneById = getById(deleteRequest.getId());
        if (oneById.getResultCode() != HttpStatus.HTTP_OK) {
            throw new BusinessException(ResultCodeConstant.DB_QUERY_NO_DATA);
        }
        CatalogDTO deleteNode = oneById.getData();
        String treePath = deleteNode.getTreePath();

        // 查询该节点下的子节点
        List<Long> deleteIdList = new ArrayList<>();
        deleteIdList.add(deleteNode.getId());
        List<CatalogDTO> catalogByPath = getCatalogByPath(treePath);
        if (CollectionUtil.isNotEmpty(catalogByPath)) {
            catalogByPath.forEach(dto -> {
                deleteIdList.add(dto.getId());
            });
        }

        // 删除该节点下的所有目录
        Integer deleteCount = catalogMapper.deleteBatchIds(deleteIdList);
        if (!deleteCount.equals(deleteIdList.size())) {
            throw new BusinessException(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
        }

        // 查询与删除节点同一层级的节点
        Integer level = deleteNode.getLevel();
        int lastIndex = treePath.lastIndexOf("/");
        String likePath = treePath.substring(0,lastIndex);
        List<CatalogDTO> sortNodeList = getByLevelAndPath(likePath, level);
        if (CollectionUtil.isNotEmpty(sortNodeList)) {
            Integer currSortNum = deleteNode.getSortNum();
            for (CatalogDTO target : sortNodeList) {
                if (target.getSortNum() > currSortNum) {
                    target.setSortNum(currSortNum);
                    currSortNum++;
                }
            }

            // 更新节点的sortNum
            List<Catalog> updateList = ListUtil.listConvert(sortNodeList, Catalog.class);
            Integer updateCount = catalogMapper.batchUpdate(updateList);
            if (!updateCount.equals(updateList.size())) {
                throw new BusinessException(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
            }
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<CatalogDTO> getById(Long id) {
        if (ObjectUtil.isNull(id)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Catalog oneById = catalogMapper.selectById(id);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        CatalogDTO catalogDTO = new CatalogDTO();
        BeanUtil.copyProperties(oneById,catalogDTO);
        return ResultDTO.getSuccessResult(catalogDTO);
    }

    @Override
    public ResultDTO<CatalogDTO> getCatalogTree(String catalogName, Integer level) {
        if (StringUtils.isBlank(catalogName)) {
            throw new BusinessException(new ResultCodeDTO(91132,"catalog name is not null","目录名称不能为空"));
        }
        if (ObjectUtil.isNull(level)) {
            throw new BusinessException(new ResultCodeDTO(91132,"level is not null","目录层级不能为空"));
        }

        List<CatalogDTO> catalogDTOList = new ArrayList<>();
        CatalogDTO rootCatalog = getCatalogByNameAndLevel(catalogName, level);
        if (ObjectUtil.isNull(rootCatalog)) {
            throw new BusinessException(ResultCodeConstant.DB_QUERY_NO_DATA);
        }
        catalogDTOList.add(rootCatalog);

        String treePath = rootCatalog.getTreePath();
        List<CatalogDTO> catalogByPath = getCatalogByPath(treePath);
        catalogByPath.forEach(dto -> {
            catalogDTOList.add(dto);
        });

        List<CatalogDTO> catalogTree = buildTreeWithMap(catalogDTOList, rootCatalog.getParentId());
        // 默认根节点唯一
        return ResultDTO.getSuccessResult(catalogTree.get(0));
    }

    /**
     * 前序遍历建树
     */
    private List<CatalogDTO> buildTreeWithRecursion(List<CatalogDTO> catalogDTOList,Long parentId) {
        List<CatalogDTO> catalogTree = new ArrayList<>();
        if (CollectionUtil.isEmpty(catalogDTOList)) {
            return catalogTree;
        }

        for (CatalogDTO catalogDTO : catalogDTOList) {
            if (catalogDTO.getParentId().equals(parentId)) {
                List<CatalogDTO> childList = this.buildTreeWithRecursion(catalogDTOList, catalogDTO.getId());
                catalogDTO.setChildCatalogList(childList);
                catalogTree.add(catalogDTO);
            }
        }
        return catalogTree;
    }

    /**
     * 层序遍历建树
     */
    private List<CatalogDTO> buildTreeWithTraverse(List<CatalogDTO> catalogDTOList,Long rootId) {
        List<CatalogDTO> catalogTree = new ArrayList<>();
        if (CollectionUtil.isEmpty(catalogDTOList)) {
            return catalogTree;
        }

        Map<Long, CatalogDTO> treeMap = catalogDTOList.stream().collect(Collectors.toMap(CatalogDTO::getId, dto -> dto));
        Queue<CatalogDTO> queue = new LinkedList<>(catalogDTOList);

        while (!queue.isEmpty()) {
            CatalogDTO node = queue.poll();
            if (node.getParentId().equals(rootId)) {
                // 根节点
                catalogTree.add(node);
            } else {
                // 非根节点，把它自己存到到父节点的childList下
                Long parentId = node.getParentId();
                CatalogDTO catalogDTO = treeMap.get(parentId);
                if (CollectionUtil.isEmpty(catalogDTO.getChildCatalogList())) {
                    List<CatalogDTO> childList = new ArrayList<>();
                    childList.add(node);
                    catalogDTO.setChildCatalogList(childList);
                } else {
                    List<CatalogDTO> childList = catalogDTO.getChildCatalogList();
                    childList.add(node);
                    catalogDTO.setChildCatalogList(childList);
                }
            }
        }
        return catalogTree;
    }

    /**
     * 哈希表优化前序遍历建树
     */
    private List<CatalogDTO> buildTreeWithMap(List<CatalogDTO> catalogDTOList, Long parentId) {
        List<CatalogDTO> catalogTree = new ArrayList<>();
        if (CollectionUtil.isEmpty(catalogDTOList)) {
            return catalogTree;
        }

        Map<Long, List<CatalogDTO>> treeMap = new HashMap<>();
        for (CatalogDTO catalogDTO : catalogDTOList) {
            if (catalogDTO.getParentId().equals(parentId)) {
                catalogTree.add(catalogDTO);
            } else {
                treeMap.computeIfAbsent(catalogDTO.getParentId(), k -> new ArrayList<>())
                        .add(catalogDTO);
            }
        }

        for (CatalogDTO catalogDTO : catalogDTOList) {
            List<CatalogDTO> childNodeList = getChildNodeList(catalogDTO.getId(), treeMap);
            catalogDTO.setChildCatalogList(childNodeList);
        }
        return catalogTree;
    }

    private List<CatalogDTO> getChildNodeList(Long parentId,Map<Long, List<CatalogDTO>> treeMap) {
        List<CatalogDTO> catalogDTOList = treeMap.get(parentId);
        if (CollectionUtil.isNotEmpty(catalogDTOList)) {
            for (CatalogDTO catalogDTO : catalogDTOList) {
                catalogDTO.setChildCatalogList(this.getChildNodeList(catalogDTO.getId(), treeMap));
            }
        }

        return catalogDTOList;
    }

    private List<CatalogDTO> getByParentId(Long parentId) {
        if (ObjectUtil.isNull(parentId)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<Catalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Catalog::getParentId, parentId);
        List<Catalog> catalogList = catalogMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(catalogList)) {
            return new ArrayList<>();
        }
        List<CatalogDTO> catalogDTOList = ListUtil.listConvert(catalogList, CatalogDTO.class);
        return catalogDTOList;
    }

    private CatalogDTO getCatalogByNameAndLevel(String catalogName,Integer level) {
        LambdaQueryWrapper<Catalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Catalog::getCatalogName,catalogName)
                .eq(Catalog::getLevel,level);
        List<Catalog> catalogList = catalogMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(catalogList)) {
            return null;
        }

        Catalog catalog = catalogList.get(0);
        CatalogDTO catalogDTO = new CatalogDTO();
        BeanUtil.copyProperties(catalog,catalogDTO);
        return catalogDTO;
    }

    private List<CatalogDTO> getCatalogByPath(String treePath) {
        if (StringUtils.isBlank(treePath)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<Catalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Catalog::getTreePath,treePath + "/")
                .orderByAsc(Catalog::getLevel)
                .orderByAsc(Catalog::getSortNum);
        List<Catalog> catalogList = catalogMapper.selectList(wrapper);
        List<CatalogDTO> catalogDTOList = ListUtil.listConvert(catalogList, CatalogDTO.class);
        return catalogDTOList;
    }

    private List<CatalogDTO> getByLevelAndPath(String treePath, Integer level) {
        if (StringUtils.isBlank(treePath)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }
        if (ObjectUtil.isNull(level)) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<Catalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Catalog::getTreePath,treePath + "/")
                .eq(Catalog::getLevel,level)
                .orderByAsc(Catalog::getSortNum);
        List<Catalog> catalogList = catalogMapper.selectList(wrapper);
        List<CatalogDTO> catalogDTOList = ListUtil.listConvert(catalogList, CatalogDTO.class);
        return catalogDTOList;
    }

}

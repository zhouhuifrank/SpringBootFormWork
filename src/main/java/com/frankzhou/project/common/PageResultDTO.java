package com.frankzhou.project.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: this.FrankZhou
 * @date: 2012/12/27
 * @description: 分页查询结果通用返回类
 */
@Data
@NoArgsConstructor
public class PageResultDTO<T extends List<?>> extends ResultDTO<T> {
    private static final Long serialVersionUID = 1L;
    private T pageResult;
    private Integer pageCount;

    public PageResultDTO(Integer pageCount, T pageResult, Integer resultCode, String error, String errorMsg) {
        super(null,resultCode,error,errorMsg,true,null);
        this.pageCount = pageCount;
        this.pageResult = pageResult;
    }

    public static <T> PageResultDTO<List<T>> getErrorPageResult(ResultDTO resultDTO) {
        Integer resultCode = resultDTO.getResultCode();
        String error = resultDTO.getError();
        String errorMsg = resultDTO.getErrorMsg();
        return new PageResultDTO<>(0,(List)null,resultCode,error,errorMsg);
    }

    public static <T> PageResultDTO<List<T>> getErrorPageResult(ResultCodeDTO codeDTO) {
        return getResult(0,(List)null,codeDTO);
    }

    public static <T> PageResultDTO<List<T>> getSuccessPageResult() {
        return getResult(0,new ArrayList<>(0),ResultCodeConstant.SUCCESS);
    }

    public static <T> PageResultDTO<List<T>> getSuccessPageResult(Integer pageCount, List<T> pageResult) {
        return getResult(pageCount,pageResult,ResultCodeConstant.SUCCESS);
    }

    public static <T> PageResultDTO<List<T>> getResult(Integer pageCount,List<T> pageResult,ResultCodeDTO resultCodeDTO) {
        return new PageResultDTO(pageCount,pageResult,resultCodeDTO.getCode(),resultCodeDTO.getMessage(),resultCodeDTO.getMessageInfo());
    }

}

package com.frankzhou.project.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: this.FrankZhou
 * @date: 2012/12/27
 * @description: 前端通用返回类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO<T> implements Serializable {
    private static final Long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应状态码 200表示ok 其他表示异常
     */
    private Integer resultCode;

    /**
     * 错误英文描述
     */
    private String error;

    /**
     * 错误中文描述
     */
    private String errorMsg;

    /**
     *  是否请求成功
     */
    private Boolean isSuccess;

    /**
     * 缓存数据
     */
    private Map<String,Object> cacheMap = new HashMap<>();


    public static <T> ResultDTO<T> getResult(ResultCodeDTO resultCodeDTO) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setResultCode(resultCodeDTO.getCode());
        result.setError(resultCodeDTO.getMessage());
        result.setErrorMsg(resultCodeDTO.getMessageInfo());
        result.isSuccess = false;

        return result;
    }

    public static <T> ResultDTO<T> getSuccessResult() {
        ResultDTO<T> result = new ResultDTO<>();
        result.setResultCode(200);
        result.setIsSuccess(true);

        return result;
    }

    public static <T> ResultDTO<T> getSuccessResult(T data) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setResultCode(200);
        result.setData(data);
        result.setIsSuccess(true);

        return result;
    }

    public static <T> ResultDTO<T> getErrorResult(ResultCodeDTO resultCodeDTO) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setResultCode(resultCodeDTO.getCode());
        result.setError(resultCodeDTO.getMessage());
        result.setErrorMsg(resultCodeDTO.getMessageInfo());
        result.isSuccess = false;

        return result;
    }

    public ResultDTO<T> addMap(String key, Object value) {
        this.cacheMap.put(key,value);
        return this;
    }

    private Boolean requestSuccessIs() {
        return this.isSuccess == true;
    }
}

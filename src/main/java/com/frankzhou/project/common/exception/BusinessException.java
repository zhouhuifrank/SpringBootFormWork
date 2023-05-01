package com.frankzhou.project.common.exception;

import com.frankzhou.project.common.ResultCodeDTO;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 自定义异常类
 * @date 2023-04-21
 */
public class BusinessException extends RuntimeException {

    private Integer code;

    private String info;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message,Integer code,String info) {
        super(message);
        this.info = info;
        this.code = code;
    }

    public BusinessException(ResultCodeDTO codeDTO) {
        super(codeDTO.getMessageInfo());
        this.info = codeDTO.getMessageInfo();
        this.code = codeDTO.getCode();
    }

    public BusinessException(ResultCodeDTO codeDTO,String message) {
        super(message);
        this.code = codeDTO.getCode();
        this.info = codeDTO.getMessageInfo();
    }

    public Integer getCode() {
        return this.code;
    }

    public String getInfo() {
        return this.info;
    }
}

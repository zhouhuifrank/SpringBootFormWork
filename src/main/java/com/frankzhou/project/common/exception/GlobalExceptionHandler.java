package com.frankzhou.project.common.exception;

import com.frankzhou.project.common.ResultCodeDTO;
import com.frankzhou.project.common.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 全局异常处理
 * @date 2023-04-21
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultDTO<?> businessExceptionHandler(BusinessException e) {
        log.warn("business exception: {}",e.getMessage());
        return ResultDTO.getResult(new ResultCodeDTO(e.getCode(),e.getInfo(),e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResultDTO<?> runtimeExceptionHandler(RuntimeException e) {
        log.warn("runtime exception:{}",e.getMessage());
        return ResultDTO.getResult(new ResultCodeDTO(9999,"system exception",e.getMessage()));
    }
}

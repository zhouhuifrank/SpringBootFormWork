package com.frankzhou.project.common;

/**
 * @author: this.FrankZhou
 * @date: 2012/12/27
 * @description: 系统错误码
 */
public class ResultCodeConstant {
    public static final ResultCodeDTO SUCCESS = new ResultCodeDTO(200,"success","请求成功");

    // 请求相关错误码

    public static final ResultCodeDTO REQUEST_PARAM_ERROR = new ResultCodeDTO(301,"request param is wrong","请求参数错误");

    public static final ResultCodeDTO SYSTEM_ERROR = new ResultCodeDTO(302,"system error","系统异常");

    // 业务相关错误码


    // 数据库相关错误码

    public static final ResultCodeDTO DB_QUERY_NO_DATA = new ResultCodeDTO(601,"database query no data","数据库查询无此数据");

    public static final ResultCodeDTO DB_INSERT_COUNT_ERROR = new ResultCodeDTO(602,"database insert count error","数据库插入条数错误");

    public static final ResultCodeDTO DB_UPDATE_COUNT_ERROR = new ResultCodeDTO(603,"database update count error","数据库更新条数错误");

    public static final ResultCodeDTO DB_DELETE_COUNT_ERROR = new ResultCodeDTO(604,"database delete count error","数据库删除条数错误");

    // Excel读取错误

    public static final ResultCodeDTO EXCEL_READ_ERROR = new ResultCodeDTO(701,"excel read error","excel解析错误");

    public static final ResultCodeDTO EXCEL_WRITE_ERROR = new ResultCodeDTO(702,"excel write error","excel表格写出错误");

    // 权限错误

    public static final ResultCodeDTO NO_AUTH_ERROR = new ResultCodeDTO(801,"no auth error","用户没有权限");

    ResultCodeConstant() {
    }
}

package com.frankzhou.project.model.excel.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户excel导出信息
 * @date 2023-05-03
 */
@Data
@ExcelIgnoreUnannotated
public class UserExcelDTO {

    @ExcelIgnore
    private Long id;

    @ExcelProperty(index = 0)
    private String userName;

    @ExcelProperty(index = 1)
    private String userAccount;

    @ExcelIgnore
    private String userAvatar;

    @ExcelProperty(index = 2)
    private String gender;

    @ExcelProperty(index = 3)
    private String phone;

    @ExcelProperty(index = 4)
    private String email;

    @ExcelProperty(index = 5)
    private String userRole;

    @ExcelIgnore
    private String userPassword;

    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Date updateTime;

    @ExcelIgnore
    private Integer isDelete;

}

package com.frankzhou.project.model.dto.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-04-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAddRequest {

    private Integer age;

    private Integer gender;

    private String education;

    private String place;

    private String job;

    private String contact;

    private String loveExperience;

    private String content;

    private String photo;

    private Integer reviewStatus;

    private String reviewMessage;

    private Integer viewNum;

    private Integer collectNum;

    private Integer thumbNum;
}

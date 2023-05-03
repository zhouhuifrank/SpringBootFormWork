package com.frankzhou.project.manager;

import cn.hutool.json.JSONUtil;
import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.eunms.PostGenderStatusEnum;
import com.frankzhou.project.common.eunms.UserRoleEnum;
import com.frankzhou.project.model.dto.user.UserAddDTO;
import com.frankzhou.project.model.dto.user.UserQueryDTO;
import com.frankzhou.project.model.dto.user.UserUpdateDTO;
import com.frankzhou.project.model.entity.User;
import com.frankzhou.project.model.vo.UserVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-05-03
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserManagerTests {

    @Resource
    private UserManager userManager;

    @Test
    public void testInsertUser() {
        UserAddDTO addDTO = new UserAddDTO();
        addDTO.setGender(PostGenderStatusEnum.MALE.getValue());
        addDTO.setUserAccount("jiange");
        addDTO.setUserPassword("woaini");
        addDTO.setUserRole("admin");
        addDTO.setPhone("19858119388");
        ResultDTO<Boolean> result = userManager.insertOne(addDTO);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testGetUserById() {
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setId(4L);
        ResultDTO<UserVO> result = userManager.getById(queryDTO);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testUpdateUserById() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setId(6L);
        updateDTO.setGender(PostGenderStatusEnum.FEMALE.getValue());
        updateDTO.setPhone("123456789");
        updateDTO.setUserName("魔法少女");
        ResultDTO<Boolean> result = userManager.updateById(updateDTO);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testDeleteUserById() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setId(4L);
        ResultDTO<Boolean> result = userManager.deleteById(deleteRequest);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testBatchInsert() {

    }

    @Test
    public void testBatchUpdate() {

    }

    @Test
    public void testBatchDelete() {

    }

    @Test
    public void testGetListByCond() {
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setGender(PostGenderStatusEnum.MALE.getValue());
        queryDTO.setUserRole(UserRoleEnum.ADMIN_ROLE.getValue());
        ResultDTO<List<UserVO>> result = userManager.getListByCond(queryDTO);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void testGetPageListByCond() {
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setCurrPage(1);
        queryDTO.setPageSize(5);
        queryDTO.setUserRole(UserRoleEnum.ADMIN_ROLE.getValue());
        PageResultDTO<List<UserVO>> result = userManager.getPageListByCond(queryDTO);
        System.out.println(JSONUtil.toJsonStr(result));
    }
}

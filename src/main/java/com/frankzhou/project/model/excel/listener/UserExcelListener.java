package com.frankzhou.project.model.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.event.Handler;
import com.frankzhou.project.model.vo.UserVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户信息Excel监听器
 * @date 2023-05-03
 */
@Slf4j
public class UserExcelListener extends AnalysisEventListener<UserVO> {

    private boolean isComplete = false;

    private final int BATCH_SIZE = 100;

    private List<UserVO> cachedDataList = new ArrayList<>();

    public static String[] headerCn = new String[] {
            "用户昵称",
            "账号",
            "性别",
            "手机号",
            "邮箱",
            "权限"
    };

    private Map<Integer,String> checkHeadMap = new HashMap<>();

    /**
     * 解析excel
     */
    @Override
    public void invoke(UserVO userVO, AnalysisContext analysisContext) {
        this.cachedDataList.add(userVO);
    }

    /**
     * 解析表头
     */
    @Override
    public void invokeHeadMap(Map<Integer,String> headMap,AnalysisContext analysisContext) {
        for (int i=0;i<headerCn.length;i++) {
            if (!headMap.get(i).equalsIgnoreCase(headerCn[i])) {
                checkHeadMap.put(i,headMap.get(i)+"and"+headerCn[i]);
            }
        }
    }

    /**
     * 解析完成，数据存入dataList中
     * 当业务不复杂的时候，可以在这里写一些业务逻辑落库
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        isComplete = true;
        log.info("excel解析完成");
    }

    public boolean isCompleted() {
        return isComplete == true;
    }

    public List<UserVO> getDataList() {
        return this.cachedDataList;
    }

    public Map<Integer,String> getHeaderMap() {
        return this.checkHeadMap;
    }
}

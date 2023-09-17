package com.frankzhou.project.Util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.frankzhou.project.model.entity.TestExcel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description EasyExcel多列表组合填充
 * @date 2023-07-04
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExcelTests {

    @Test
    public void testDownloadExcel() {
        // 需要生成excel的对象
        TestExcel testExcel = TestExcel.builder()
                .problemNo("SG20230625-0001")
                .level("2")
                .problemTitle("测试问题单")
                .createdUser("zhou")
                .createdTime(new Date())
                .problemDesc("测试问题单")
                .dutyOperNo("hui")
                .build();


        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // {} 代表普通变量 {.} 代表是list的变量 {前缀.} 前缀可以区分不同的list
        String templateFileName = "D:\\TechLearn" + File.separator + "dataQualitySheet.xlsx";
        //String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "composite.xlsx";

        String targetFileName = "D:\\TechLearn" + File.separator + "dataQualityProblemSheet.xlsx";
        // String fileName = TestFileUtil.getPath() + "compositeFill" + System.currentTimeMillis() + ".xlsx";

        try (ExcelWriter excelWriter = EasyExcel.write(targetFileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
            // 根据对象填入数据
            Map<String,Object> map = new HashMap<>();
            map.put("problemNo",testExcel.getProblemNo());
            map.put("level",testExcel.getLevel());
            map.put("problemTitle",testExcel.getProblemTitle());
            map.put("createdUser",testExcel.getCreatedUser());
            map.put("createdTime","2023-01-2");
            map.put("problemDesc",testExcel.getProblemDesc());
            map.put("dutyOperNo",testExcel.getDutyOperNo());

            excelWriter.fill(map,writeSheet);
        }
        log.info("导出文件成功");
    }

    @Test
    public void testMergeExcel() {
        // 需要生成excel的对象
        TestExcel testExcel = TestExcel.builder()
                .problemNo("SG20230625-0001")
                .level("2")
                .problemTitle("测试问题单")
                .createdUser("zhou")
                .createdTime(new Date())
                .problemDesc("测试问题单")
                .dutyOperNo("hui")
                .build();


        String templateFileName = "D:\\TechLearn" + File.separator + "dataQualitySheet2.xlsx";

        String targetFileName = "D:\\TechLearn" + File.separator + "dataQualityProblemSheet2.xlsx";

        try (ExcelWriter excelWriter = EasyExcel.write(targetFileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
            // 根据对象填入数据
            Map<String,Object> map = new HashMap<>();
            map.put("problemNo",testExcel.getProblemNo());
            map.put("level",testExcel.getLevel());
            map.put("problemTitle",testExcel.getProblemTitle());
            map.put("createdUser",testExcel.getCreatedUser());
            map.put("createdTime","2023-01-2");
            map.put("problemDesc",testExcel.getProblemDesc());
            map.put("dutyOperNo",testExcel.getDutyOperNo());
            map.put("merge","合并单元格测试");

            excelWriter.fill(map,writeSheet);
        }
        log.info("导出文件成功");
    }
}

package com.frankzhou.project.Util;

import cn.hutool.db.sql.SqlFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-08-27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlUtilTests {

    @Test
    public void testFormatSql() {
//        String sql = "update data_asset_info where     type_id = 1 and     status = 'NORMAL'\n" +
//                "and asset_id   in \n" +
//                "(select id from data_asset_dbt_base where status = 'DELETED' and parent_code is not null);";

        String sql = "select id,asset_id,name_cn,name_en,catalog_name,system_code,system_name,create_user,create_time from data_asset_info\n" +
                "where parent_code != null and statys = 'NORMAL';";
        String formatSql = SqlFormatter.format(sql);
        log.info(formatSql);
    }
}

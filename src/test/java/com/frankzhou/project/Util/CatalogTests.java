package com.frankzhou.project.Util;

import com.frankzhou.project.service.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-10
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CatalogTests {

    @Resource
    private CatalogService catalogService;

}

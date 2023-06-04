package com.frankzhou.project.common.eunms;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 数据库类型枚举类
 * @date 2023-05-19
 */
@Getter
public enum DbTypeEnum {

    Mysql("Mysql","com.mysql.cj.jdbc.Driver"),
    Oracle("Oracle","oracle.jdbc.driver.OracleDriver"),
    Postgresql("Postgresql","org.postgresql.Driver"),
    Adb("AnalyticDB",""),
    Hive("Hive","org.apache.hive.jdbc.HiveDriver"),
    Impala("Impala","com.cloudera.impala.jdbc41.Driver");

    private final String dbName;

    private final String driverClassName;

    DbTypeEnum(String dbName,String driverClassName) {
        this.dbName = dbName;
        this.driverClassName = driverClassName;
    }

    public static List<String> getDriverList() {
        List<String> driverList = Arrays.stream(DbTypeEnum.values()).map(DbTypeEnum::getDriverClassName).collect(Collectors.toList());
        return driverList;
    }

    public static DbTypeEnum getDb(String dbName) {
        DbTypeEnum[] values = DbTypeEnum.values();
        for (DbTypeEnum value : values) {
            if (value.getDbName().equals(dbName)) {
                return value;
            }
        }
        return null;
    }

}

package com.cuteximi.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @program: hbase
 * @description: HBase 操作，练习
 * @author: TSL
 * @create: 2019-01-15 15:49
 **/
public class HBaseAPI {

    private static final String TABLE_NAME = "Table_Name";
    private static final String CF_DEFAULT = "cf";

    HBaseAdmin admin;

    /**
     * 初始化
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        // 配置信息
        Configuration configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum","node01:2181,node02:2181,node03:2181");
        // 创建 HBaseAdmin
        admin = new HBaseAdmin(configuration);

    }

    /**
     * 创建表
     * @throws IOException
     */
    @Test
    public void createTable() throws IOException {
        // 表的描述
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));

        // 列族的描述

        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(CF_DEFAULT);

        // 设置开启缓存，将表放到RegionServer的缓存中，保证在读取的时候被cache命中。

        hColumnDescriptor.setInMemory(true);

        // 把列族信息加入到 HTableDescriptor 中

        hTableDescriptor.addFamily(hColumnDescriptor);

        // 创建表,如果存在则删除，重新创建

        if (admin.tableExists(hTableDescriptor.getName())){
            admin.disableTable(hTableDescriptor.getName());
            admin.deleteTable(hTableDescriptor.getName());
        }

        admin.createTable(hTableDescriptor);

    }


}

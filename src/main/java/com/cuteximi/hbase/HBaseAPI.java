package com.cuteximi.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: hbase
 * @description: HBase 操作，练习
 * @author: TSL
 * @create: 2019-01-15 15:49
 **/
public class HBaseAPI {

    private static final String TABLE_NAME = "Table_Name";
    private static final String CF_DEFAULT = "cf1";

    HBaseAdmin admin;

    // 构建一个对象来映射 HBase 的一张表
    // 注意这个对象不是线程安全的
    HTable hTable;

    // 配置信息
    Configuration configuration;

    /**
     * 0
     * 初始化
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        // 配置信息
        configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum","node01:2181,node02:2181,node03:2181");

        // 创建 HBaseAdmin
        admin = new HBaseAdmin(configuration);


    }

    /**
     * 1
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

    /**
     * 2
     * 插入数据 && 批量插入
     *
     * @throws Exception，
     */
    @Test
    public void insertDB() throws Exception {

        // Creates an object to access a HBase table.
        // Shares zookeeper connection and other resources with other HTable instances
        // HTable 对象共享配置的好处：共享ZooKeeper的连接；共享公共的资源：客户端需要通过ZooKeeper查找-ROOT-和.META.表，
        // 这个需要网络传输开销，客户端缓存这些公共资源后能够减少后续的网络传输开销，加快查找过程速度
        hTable = new HTable(configuration,TABLE_NAME);

        // 即使是高负载的多线程程序，也并没有发现因为共享Configuration而导致的性能问题；[这句话引用自网络，自己没有测试]

        byte[] rowKey = "ID123-20180908".getBytes();

        Put put = new Put(rowKey);

        put.add(CF_DEFAULT.getBytes(), "name".getBytes(), "tsl".getBytes());
        put.add(CF_DEFAULT.getBytes(), "age".getBytes(), "23".getBytes());
        put.add(CF_DEFAULT.getBytes(), "sex".getBytes(), "man".getBytes());



        Put put2 = new Put(rowKey);

        put2.add(CF_DEFAULT.getBytes(), "name".getBytes(), "dgm".getBytes());
        put2.add(CF_DEFAULT.getBytes(), "age".getBytes(), "23".getBytes());
        put2.add(CF_DEFAULT.getBytes(), "sex".getBytes(), "woman".getBytes());

        // 单条插入
        //hTable.put(put);

        // 为了节省资源的占用，可以批量插入，比如
        List<Put> list = new ArrayList<Put>();

        list.add(put);
        list.add(put2);

        hTable.put(list);


    }


}

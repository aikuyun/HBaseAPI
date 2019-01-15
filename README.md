# HBaseAPI
使用 Java 操作 HBase API, 包括建表、插入数据（推荐批量），查询数据（使用批量查，范围查） 

## 创建表

1. ZK集群的配置信息
2. 获取 HBaseAdmin 对象
3. 构造 HTableDescriptor 对象
4. 构造 HColumnDescriptor 对象
5. 使用 createTable(HTableDescriptor hTableDescriptor) 方法。当然还有其他的选择。比如传入的参数还可以加上一个 splitKeys 的二维字节数组，还可以加上
startKey, endKey, numRegions 这三个参数。还可以异步的创建表格。

具体可以参考代码段：


解释：

**HBaseAdmin** Provides an interface to manage HBase database table metadata + general administrative functions. 

**HTableDescriptor**contains the details about an HBase table  such as the descriptors of all the column families, is the table a catalog table, <code> -ROOT- </code> or
<code> hbase:meta </code>, if the table is read only, the maximum size of the memstore, when the region split should occur, coprocessors associated with it etc...
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

**HBaseAdmin** 管理元数据，并包含一些管理表的方法。

**HTableDescriptor** 表的描述，可以传入表的 name

**HColumnDescriptor** 添加列族，设置缓存


## 插入数据 && 批量插入

参考：https://www.cnblogs.com/likehua/p/3976349.html

HTable和HTablePool都是HBase客户端API的一部分，可以使用它们对HBase表进行CRUD操作。

1.   规避HTable对象的创建开销

因为客户端创建HTable对象后，需要进行一系列的操作：检查.META.表确认指定名称的HBase表是否存在，表是否有效等等，整个时间开销比较重，可能会耗时几秒钟之长，因此最好在程序启动时一次性创建完成需要的HTable对象，如果使用Java API，一般来说是在构造函数中进行创建，程序启动后直接重用。

2.   HTable对象不是线程安全的

HTable对象对于客户端读写数据来说不是线程安全的，因此多线程时，要为每个线程单独创建复用一个HTable对象，不同对象间不要共享HTable对象使用，特别是在客户端auto flash被置为false时，由于存在本地write buffer，可能导致数据不一致。

3.   HTable对象之间共享Configuration

HTable对象共享Configuration对象，这样的好处在于：

共享ZooKeeper的连接：每个客户端需要与ZooKeeper建立连接，查询用户的table regions位置，这些信息可以在连接建立后缓存起来共享使用；
共享公共的资源：客户端需要通过ZooKeeper查找-ROOT-和.META.表，这个需要网络传输开销，客户端缓存这些公共资源后能够减少后续的网络传输开销，加快查找过程速度。

为了解决线程安全的问题：

> HTablePool 解决HTable存在的线程不安全问题，同时通过维护固定数量的HTable对象，能够在程序运行期间复用这些HTable资源对象

1.   HTablePool可以自动创建HTable对象，而且对客户端来说使用上是完全透明的，可以避免多线程间数据并发修改问题。

2.   HTablePool中的HTable对象之间是公用Configuration连接的，能够可以减少网络开销。

HTablePool的使用很简单：每次进行操作前，通过HTablePool的getTable方法取得一个HTable对象，然后进行put/get/scan/delete等操作，最后通过HTablePool的putTable方法将HTable对象放回到HTablePool中。


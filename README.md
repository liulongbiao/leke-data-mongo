# leke-data-mongo

基于 mongo-java-driver 3.0 和 spring 的 MongoDB 类库封装。

## 使用说明

### 获取 `MongoClient`

```java
@Bean
public MongoClientFactoryBean mongo() throws Exception {
  MongoClientFactoryBean factory = new MongoClientFactoryBean();
  factory.setReplicaset(replicaset);
  factory.setCredentials(credentials);
  return factory;
}

@Bean
public MongoDatabase db(MongoClient mongo) throws Exception {
  return mongo.getDatabase(dbName);
}
```

`MongoClientFactoryBean` 可用于获取 `MongoClient` 对象。
它给生成的 `MongoClient` 添加了 `BeanCodecProvider`，可用于转换任意类型的 JavaBean 为 Document 的编码器。

其参数：

* `replicaset` - (必需)副本集种子地址，地址格式为 `ip:port`，多个地址间以 `,` 分隔。
* `credentials` - (可选)身份凭证，格式为 `user:db:password`，若有多个凭证可以以 `,` 分隔。

### JavaBean 编写

JavaBean 的字段可添加注解 `@_id` 将字段标识为对应 MongoDB 主键 `_id` 字段。

JavaBean 的字段可添加注解 `@ObjectId` 将字段标识为需转换为 `ObjectId` 类型。
这在数据库中存放  `ObjectId` 类型，而 JavaBean 中希望显示 `String` 类型时很有用。

```java
public class JavaBean {
  @_id
  private org.bson.types.ObjectId id; // 对应 _id 字段
  @ObjectId
  private String refId; // 数据库中存放 ObjectId 类型
  // getters and setters
}
```

### 数据操作

由于注册了 `BeanCodec`，因此可以直接使用 mongo-java-driver 中原生的 `MongoCollection` 集合来进行数据操作。

```java
@Repository
public class ArchiveService implements IArchiveService {
  @Autowired
  private MongoDatabase db;
  private MongoCollection<Archive> coll;

  @PostConstruct
  public void init() {
    Assert.notNull(db, "DB should not be null");
    coll = db.getCollection("archives", Archive.class);
  }

  @Override
  public void save(Archive archive) {
    coll.insertOne(archive);
  }
}
```

`MongoCollection` 的相关 API 可以查看官方文档。

### BsonUtils 工具类

提供了一个 BsonUtils 工具类将一个 JavaBean 转换成 `Document`，或者将一个 `Document` 转换成 JavaBean。

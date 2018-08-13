# CoreData
关系型数据库


##### 愿景

从名字可以看出，取名自IOS的CoreData数据库。希望能够支持Relation，并能在保证速度不会差距很大的情况下方便简单的使用。

##### 工程分析

```
base         --- 基础工程，数据库的基础模型、工具类、注解
compiler     --- 编译库，用于apt或者annotationProcessor
core         --- 核心代码库，主要用于CoreData对数据库及数据的管理
cipher       --- 加密库，支持SqlCipher对Sqlite进行加密
demo         --- 样例代码
```

##### 接入说明

1. 使用`gradle`的形式进行接入，`x.x.x`用 [![](https://jitpack.io/v/fangcloud-android/coredata.svg)](https://jitpack.io/#fangcloud-android/coredata)代替。找到需要引入CoreData工程的`build.gradle`，在`dependencies`内添加如下代码

   > 接入代码生成工具

   ```groovy
   annotationProcessor 'com.github.fangcloud-android.coreData:compiler:x.x.x'
   ```
   > 接入非加密版的库

   ```groovy
   compile 'com.github.fangcloud-android.coredata:core:x.x.x'
   ```
   > 接入加密版的库

   ```groovy
   compile 'com.github.fangcloud-android.coredata:cipher:x.x.x' // 只需引用此库即可
   ```

2. 代码接入

   > 注解说明

   | 注解类     | 说明         | 作用域 | 其他                                                         |
   | ---------- | ------------ | ------ | ------------------------------------------------------------ |
   | Entity     | 实体类注解   | CLASS  | @Entity(tableName = "user", primaryKey = "magazine_id")      |
   | ColumnInfo | 纵列信息注解 | FIELD  | @ColumnInfo(name = "author_id")                              |
   | PrimaryKey | 主键的注解   | FIELD  | @PrimaryKey                                                  |
   | Ignore     | 用于忽略字段 | FIELD  | @Ignore，设置此注解的field，将不会被持久化                   |
   | Embedded   | 内嵌类注解   | FIELD  | 此注解的field，会将其内部的field作为数据库结构保存           |
   | Convert    | 数据转换器   | FIELD  | @Convert(converter = SerializableListConverter.class, dbType = String.class)，将field转换为可存储类型 |
   | Relation   | 关联注解     | FIELD  | @Relation，此注解的field必须对应一个Entity                   |

   > 示例

    ```java
   @Entity(tableName = "book")
   public class Book {
   
       @PrimaryKey
       public long id
       
       public String name;
       
       @Convert(converter = SerializableListConverter.class, dbType = String.class)
       public List<Tag> tags;
   
       @Relation
       @ColumnInfo(name = "author_id")
       public Author author;
   
       @Embedded
       public Desc desc;
   
       @Ignore
       public Bitmap icon;
   }
    ```
   > 初始化， 在Application的onCreate方法中调用初始化方法
   ```java
   public class BaseApplication extends Application {
   	
   	...
   	
       @Override
       public void onCreate() {
           super.onCreate();
           CoreData.init(this, CoreData.Builder.builder()
                   .name("名字")
                   .password("密码")// 决定是否使用加密库
                   .version(1) // 版本
                   .register(Book.class, Author.class)// 将@Entity的类都加进这里
           );
       }
       
       ...
   }
   ```
   > 增删改查
   ```java
   // 取出相应实体的CoreDao
   CoreDao<Book> bookCoreDao = CoreData.defaultInstance().dao(Book.class);
   // 插入
   Book book;
   Collection<Book> bookCollection;
   bookCoreDao.replace(book);
   bookCoreDao.replace(bookCollection);
   // 删除
   boolean success = bookCoreDao.deleteByKey(bookId);// 根据给定主键删除
   boolean success = bookCoreDao.deleteByKeys(new Long[]{bookId_1, bookId_2, bookId_3});// 根据给定主键数组删除
   boolean success = bookCoreDao.deleteAll(); // 删除全部
   // 查询
   Book book = bookCoreDao.queryByKey(bookId);// 根据给定主键查询
   List<Book> boolList = bookCoreDao.queryByKeys(new Long[]{bookId_1, bookId_2, bookId_3});// 根据给定主键数组查询
   List<Book> boolList = bookCoreDao.queryAll(); // 查询全部全部
   ```

3. 混淆

   ```txt
   # 使用CoreData的混淆代码
   -keep class * extends com.coredata.core.CoreDao
   -keep class com.coredata.annotation.Entity
   -keepnames @com.coredata.annotation.Entity class *
   
   # 如果使用到加密库，请将下面的代码也做配置
   -keepclasseswithmembers class com.coredata.cipher.CipherOpenHelper {
       public <init>(...);
   }
   -keep class net.sqlcipher.** { *; }
   -keep class net.sqlcipher.database.** { *; }
   ```

4. RxJava支持

   > O.1.3-SNAPSHOT 版本支持了rxjava，支持表级别的数据变动监听
   ```java
   private Disposable disposable;
   public void register(View view) {
      if (disposable != null) {
          return;
      }
      disposable = CoreData.defaultInstance()
          .dao(Book.class)
          .query() // 此处还可以继续补充查询条件
          .observable()
          .subscribe(new Consumer<List<Book>>() {
              @Override
              public void accept(List<Book> books) {
                  // 此处处理收到的数据，subdcribe就会收到第一次数据
                  // 当数据库变动影响到所查询的数据时，还会收到
              }
          });
   }
   
   public void unRegister(View view) {
      if (disposable != null) {
          disposable.dispose();
      }
      disposable = null;
   }
   ```

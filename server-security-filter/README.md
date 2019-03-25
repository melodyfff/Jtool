# server-security-filter 

通用`Java Servlet`安全过滤器

参考项目： [cas-server-security-filter](https://github.com/apereo/cas-server-security-filter)

仅供学习使用



## 日志部分

```xml
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>${jcl-over-slf4j.version}</version>
      </dependency>

      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jul-to-slf4j</artifactId>
        <version>${jul-to-slf4j.version}</version>
      </dependency>
```

**JCL:** 又叫做`Common Logging`，`Apache common logging`，`Jakarta Commons Logging`

**JUL:** `Java Util Logging`，是`java 1.4`以来自带的一个`logging`系统

`jcl-over-slf4j`：把`common logging`的jar包从cp里删掉，然后把`jcl-over-slf4j`放入cp。这个jar包中的类和`common logging`中的类名，方法名等完全一样，只是在具体的方法中，把所有的请求都暗渡陈仓的转移到了`slf4j`上

`jul-to-slf4j`: 是用自己的`Handler`（JUL处理日志的接口）作为`root`，同时删除所有的其它`logger`。这样就相当于用个二传手把所有的`log`通过这个硬塞进来的`Handler`，委托给了`slf4j`，然后`slf4j`再寻找实现

参考地址:
- 官网：https://www.slf4j.org/legacy.html
- Java的Log系统介绍和切换：https://www.cnblogs.com/softidea/p/5308670.html
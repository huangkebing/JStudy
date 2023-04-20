# Apace commons lang3

## Apache commons

Apache Commons是一个开源Java类库，旨在提供可重用的、开源的Java代码，减少开发过程中的重复工作。它包含了许多工具类库，用于解决平时编程经常会遇到的问题，例如执行外部进程的命令、压缩与解压缩文件等。Apache Commons几乎不依赖其他第三方的类库，接口稳定，集成简单，可以提高编码效率和代码质量。

官方文档：[Apache Commons](https://commons.apache.org/)

Apache Commons有很多子项目：

| 包名          | 作用                                                         |
| ------------- | ------------------------------------------------------------ |
| BCEL          | 字节码工程库——分析、创建和操作 Java 类字节码工程库——分析、创建和操作 Java 类文文件BeanUtils围绕 Java 反射和内省 API 的易于使用的包装器 |
| BeanUtils     | 围绕 Java 反射和内省 API 的易于使用的包装器                  |
| CLI           | 命令行参数解析器。Codec通用编码/解码算法（例如语音、base64、URL） |
| Collections   | 扩展或增强 Java 集合框架                                     |
| Compress      | 定义用于处理 tar、zip 和 bzip2 文件的 API                    |
| Configuration | 读取各种格式的配置/首选项文件                                |
| Crypto        | 使用 AES-NI 包装 Openssl 或 JCE  算法实现优化的加密库        |
| CSV           | 用于读写逗号分隔值文件的组件                                 |
| Daemon        | unix-daemon-like java 代码的替代调用机制                     |
| DBCP          | 数据库连接池服务                                             |
| DbUtils       | JDBC 帮助程序库                                              |
| Email         | 用于从 Java 发送电子邮件的库                                 |
| Exec          | 用于处理 Java 中外部进程执行和环境管理的 API                 |
| FileUpload    | servlet 和 Web 应用程序的文件上传功能                        |
| Geometry      | 空间和坐标                                                   |
| Imaging       | 纯 Java 图像库                                               |
| IO            | I/O 实用程序的集合                                           |
| JCI           | Java 编译器接口JCSJava缓存系统Jelly基于 XML 的脚本和处理引擎 |
| Jexl          | 表达式语言，它扩展了 JSTL 的表达式语言                       |
| Lang          | 为 java.lang 中的类提供额外的功能                            |
| Logging       | 包装各种日志 API 实现                                        |
| Math          | 轻量级、自包含的数学和统计组件                               |
| Net           | 网络实用程序和协议实现的集合                                 |
| Numbers       | 数字类型（复数、四元数、分数）和实用程序（数组、组合）       |
| Pool          | 通用对象池组件                                               |
| RDF           | 可由 JVM 上的系统实现的 RDF 1.1 的通用实现                   |
| RNG           | 随机数生成器的实现                                           |
| Text          | Apache Commons Text  是一个专注于处理字符串的算法的库        |
| Validator     | 在 xml 文件中定义验证器和验证规则的框架                      |
| VFS           | 用于将文件、FTP、SMB、ZIP  等视为单个逻辑文件系统的虚拟文件系统组件 |
| Weaver        | 提供一种简单的方法来增强（编织）已编译的字节码               |

## lang3

提供一些工具方法来补充标准Java库，其包结构如下：

```html
├─org.apache.commons 
│  ├─lang3 // 提供很多静态工具方法
│  │  ├─arch // 配合ArchUtils使用，可以获取处理器的体系结构和类型，并提供方法判断处理器是否是某个结构、某个类型
│  │  ├─builder // 协助实现对象的toString()、hashCode()、equals()和compareTo()方法
│  │  ├─compare // 封装了compareTo方法，提供Predicate实例
│  │  ├─concurrent // 为多线程编程提供支持类
│  │  │  └─locks // 为多线程编程提供支持类
│  │  ├─event // 提供一些基于事件的工具
│  │  ├─exception // 获取exception信息的方法
│  │  ├─function // 提供允许抛出异常的函数式接口，以补充java.lang.function和实用程序以与Java 8 Lambdas一起使用
│  │  ├─math // 提供了分数类和一些数字工具方法
│  │  ├─mutable // 为基本数据类型和对象提供可变的包装器
│  │  ├─reflect // 反射工具包
│  │  ├─stream // 补充java.util.stream
│  │  ├─text // 包下所有类均以废弃
│  │  ├─time // 提供使用日期和持续时间的类和方法
│  │  ├─tuple // 元组类，提供了Pair和Triple
```

## 优点

1. 提供了丰富工具类，能够减少开发人员的工作量，避免重复造轮子
3. 经过广泛的使用和测试，具有一定的可靠性

## 缺点

使用的工具包可以快速实现我们的需求，但引入的工具包可能存在安全漏洞和性能问题，需要我们对其有一定的了解

如Apache commons BeanUtils，其中使用到了反射等耗时的操作，性能非常差：

![BeanUtils](commons-lang3.assets\BeanUtils.png)

因此在使用工具类之前需要做好调研工作，调研其性能、稳定性等方面，不要给系统留下隐患

## 代码

主要讲一些日常开发中可能会使用到的，或者JDK中没有提供的类和方法

### compare包

### function包

### Fraction

### NumberUtils

### mutable包

### ArrayUtils

### StringUtils

## 总结

1. 评估工具类库的优缺点：针对相同的功能，需要了解实现的逻辑，择优使用
2. 留意工具类库的问题：如fastjson，若发现使用的类库存在bug，需要及时评估是否会影响业务和性能，如有必要需要及时升级版本或者使用其他类库替代
3. 学习工具类库的使用方法：在平时可以积累一些优秀的工具类库中的工具方法，在日常开发中便可以联想到，提高开发效率

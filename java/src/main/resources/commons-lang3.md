# Apache Commons lang

## Apache Commons简介

官方文档：[Apache Commons](https://commons.apache.org/)

`Apache Commons`是Apache软件基金会的项目，Commons的目的是提供开源的、可重用的Java组件

`Apache Commons`项目由三部分组成：

### The Commons Proper

The Commons Proper提供非常多可重用的、稳定的Java组件库。

其子项目如下：

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

可以跟据需要去挑选对应的包，但需要注意鉴别

### The Commons Sandbox

The Commons Sandbox是一个临时的项目，其中包含社区成员正在探索和测试新的功能和实用程序。它为Apache Commons社区提供了一个试验新想法的地方，收集并评估新代码，并在尝试维护新代码时为项目管理人员提供时间。新的实用程序可能会在更大的Apache Commons项目中成为一个独立的单元，也可能会闲置

### The Commons Dormant 

The Commons Dormant的主要作用是维护那些不再活跃或已经过时的项目。这些项目可能已经失去了活跃的贡献者或用户，也可能已经与其他项目合并或重命名。The Commons Dormant通过提供必要的维护和文档资源，确保这些项目仍然能够持续保持可用和可维护。

### 优缺点

提供了丰富工具类，能够减少开发人员的工作量，避免重复造轮子

经过广泛的使用和测试，具有一定的可靠性

------

工具包的质量参差不齐，引入工具包或工具方法前需要深入调研后才能使用，就这要求我们平时有一定的积累

如Apache commons的BeanUtils，虽然实现了对象的拷贝，但其中使用到了性能较差的反射，所以性能不佳：

![BeanUtils](commons-lang3.assets\BeanUtils.png)

## 主要内容 lang简介

`Apache commons lang`为java.lang API提供了大量工具方法，例如：字符串操作、数组操作、随机数生成、反射、时间日期处理等等

注意点：Commons Lang 3.0（及后续版本）与之前的版本（org.apache.commons.lang）使用不同的包（org.apache.commons.lang3），且允许Commons Lang 3与Commons Lang 同时使用，但一般都是使用lang3

lang3中有非常多的工具类和工具方法，但并不是所有的内容都能在日常开发中使用，其包结构如下：

```html
├─org.apache.commons 
│  ├─lang3 // 提供很多静态工具方法，比较著名的如StringUtils、ArrayUtils
│  │  ├─arch // 配合ArchUtils使用，可以获取处理器的体系结构和类型，并提供方法判断处理器是否是某个结构、某个类型
│  │  ├─builder // 协助实现对象的toString()、hashCode()、equals()和compareTo()方法
│  │  ├─compare // 封装了compareTo方法，提供Predicate实例
│  │  ├─concurrent // 提供了一些并发的工具
│  │  ├─event // 提供一些基于事件的工具
│  │  ├─exception // 提供一些Exception相关的方法
│  │  ├─function // 提供允许抛出异常的函数式接口，以补充java.lang.function和实用程序以与Java 8 Lambdas一起使用
│  │  ├─math // 提供了分数类和一些数字工具方法
│  │  ├─mutable // 为基本数据类型和不可变对象提供可变的包装类
│  │  ├─reflect // 反射
│  │  ├─stream // 补充java.util.stream
│  │  ├─time // 提供使用日期和持续时间的类和方法
│  │  ├─tuple // 元组类，提供了Pair和Triple
```

## 代码

### 实用型

#### NumberUtils

#### StringUtils

#### ArrayUtils

### 启发型

#### compare

#### function

#### Fraction

#### mutable

## 简单总结

使用好工具类可以达到事半功倍、简化代码的效果。但使用前需要深入调研，充分了解后再觉得是否使用，考验平时的积累
# Apache commons

## 一、概述

#### TODO 开场白

大意就是日常开发中遇到巴拉巴拉的问题，然后引出主题

### 1.1 什么是Apache commons

官方文档：[Apache Commons](https://commons.apache.org/)

`Apache Commons`是对JDK的拓展，包含了很多开源的工具，用于解决平时编程经常会遇到的问题，减少重复劳动。Apache commons包含了许多不同的模块，今天主要讲述其中使用较多的：

- Apache commons Lang
- Apache commons Collections

### 1.2 优点

1. 提供了丰富的功能和工具类，能够大大减少开发人员的工作量
2. 提供了高效的实现，能够提高代码的性能和可读性
3. 经过广泛的使用和测试，具有较高的稳定性和可靠性
4. 可以提高开发效率，避免重复造轮子

### 1.3 缺点

使用的工具包可以快速实现我们的需求，但引入的工具包可能存在安全漏洞和性能问题，需要我们对其有一定的了解

如Apache commons BeanUtils，其中使用到了反射等耗时的操做，性能非常差：

![BeanUtils](D:\code\JStudy\java\src\main\resources\Apache commons.assets\BeanUtils.png)

因此在使用工具类之前需要做好调研工作，调研其性能、稳定性等方面，不要给系统留下隐患

## 二、使用和部分实现源码



## 三、总结

1. 评估工具类库的优缺点：针对相同的功能，需要了解实现的逻辑，择优使用
2. 留意工具类库的问题：如fastjson，若发现使用的类库存在bug，需要及时评估是否会影响业务和性能，如有必要需要及时升级版本或者使用其他类库替代
3. 学习工具类库的使用方法：在平时可以积累一些优秀的工具类库中的工具方法，在日常开发中便可以联想到，提高开发效率

```
Apache: https://blog.csdn.net/weixin_41563161/article/details/117046259
https://www.zhihu.com/column/c_1395490348211298304
模板: https://blog.csdn.net/m0_63947499/article/details/125281870
```


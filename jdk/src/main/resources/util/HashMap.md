# HashMap

## 一、字段

## 二、构造器

HashMap提供了4个构造器：

### 2.1 无参构造

```java
/**
 * 以默认容量(16)和默认因子(0.75)创建一个空的<tt>HashMap</tt>
 */
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
}
```

### 2.2 初始容量构造

```java
/**
 * 以指定容量({@code initialCapacity})和默认因子(0.75)创建一个空的<tt>HashMap</tt>
 * 复用了指定初始容量和加载因子的构造
 *
 * @param  initialCapacity 指定容量
 * @throws IllegalArgumentException 如果指定容量为负数
 */
public HashMap(int initialCapacity) {
	this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
```

### 2.3 初始容量和加载因子构造

```java
/**
 * 以指定容量({@code initialCapacity})和指定加载因子({@code loadFactor})创建一个空的<tt>HashMap</tt>
 *
 * @param  initialCapacity  初始容量
 * @param  loadFactor		加载因子
 * @throws IllegalArgumentException 初始容量为负数或者加载因子为非正数
 */
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    this.loadFactor = loadFactor;
    // 将给定的初始容量改为大于且最接近的2的幂次
    this.threshold = tableSizeFor(initialCapacity);
}
```

#### ==问题1==

> **Q：为什么HashMap的容量要设置为2的幂次？**

### 2.4 map构造

## 三、内部类

### 3.1 Map.Entry

定义在Map接口中，定义了一些行为方法，Java 1.8新增了一些<u>**使用函数式接口的排序方法**</u>



## 四、方法


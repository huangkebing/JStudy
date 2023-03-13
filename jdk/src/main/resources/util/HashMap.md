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

#### ==问题1 2的幂次== 

> **Q：为什么HashMap的容量要设置为2的幂次？**

### 2.4 map构造

## 三、内部类

### 3.1 Map.Entry

定义在Map接口中，定义了一些行为方法，Java 1.8新增了一些<u>**使用函数式接口的排序方法**</u>

```java
/**
 * 一个map的实体(键值对)。<tt>Map.entrySet<tt>方法返回映射的集合视图，其元素属于此类。
 * 获取映射项引用的唯一方法是从这个集合视图的迭代器。
 * 这些<tt>Map.Entry</tt>对象在迭代期间仅<i>有效；更正式地说，如果在迭代器返回条目之后修改了后备映射，则映射条目的行为是未定义的，
 * 除非通过对映射条目的<tt>setValue<tt>操作。
 *
 * @see Map#entrySet()
 * @since 1.2
 */
interface Entry<K,V> {
    K getKey();
    V getValue();
    V setValue(V value);
    boolean equals(Object o);
    int hashCode();

    /**
     * 返回一个按key升序排序的比较器
     *
     * <p>返回的比较器是可序列化的且当比较到一个为null的key时抛出{@link NullPointerException}
     *
     * @param  <K> map的继承了{@link Comparable}key类型
     * @param  <V> map的value类型
     * @return 一个按key升序排序的比较器
     * @see Comparable
     * @since 1.8
     */
    public static <K extends Comparable<? super K>, V> Comparator<Entry<K,V>> comparingByKey() {
        //Comparator<Entry<K, V>> & Serializable表示同时强转类型成多个接口，多用于 lambda 表达式
        return (Comparator<Entry<K, V>> & Serializable)
            (c1, c2) -> c1.getKey().compareTo(c2.getKey());
    }

    /**
     * 返回一个按value升序排序的比较器
     *
     * <p>返回的比较器是可序列化的且当比较到一个为null的value时抛出{@link NullPointerException}
     *
     * @param <K> map的key类型
     * @param <V> map的继承了{@link Comparable}value类型
     * @return 一个按value升序排序的比较器
     * @see Comparable
     * @since 1.8
     */
    public static <K, V extends Comparable<? super V>> Comparator<Entry<K,V>> comparingByValue() {
        return (Comparator<Entry<K, V>> & Serializable)
            (c1, c2) -> c1.getValue().compareTo(c2.getValue());
    }

    /**
     * 返回一个按key，以给定的{@link Comparator}排序的比较器
     *
     * <p>如果给定的比较器是可序列化的，则返回的比较器也是可序列化的
     *
     * @param  <K> map的key类型
     * @param  <V> map的value类型
     * @param  cmp key的比较器{@link Comparator}
     * @return key的比较器
     * @since 1.8
     */
    public static <K, V> Comparator<Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
        Objects.requireNonNull(cmp);
        return (Comparator<Entry<K, V>> & Serializable)
            (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
    }

    /**
     * 返回一个按value，以给定的{@link Comparator}排序的比较器
     *
     * <p>如果给定的比较器是可序列化的，则返回的比较器也是可序列化的
     *
     * @param  <K> map的key类型
     * @param  <V> map的value类型
     * @param  cmp value的比较器{@link Comparator}
     * @return value的比较器
     * @since 1.8
     */
    public static <K, V> Comparator<Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
        Objects.requireNonNull(cmp);
        return (Comparator<Entry<K, V>> & Serializable)
            (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
    }
}
```

#### ==测试代码1 Map.Entry排序器==

```java
@Test
public void nodeTest(){
    HashMap<String, Integer> map = new HashMap<>();
    map.put("8",1);map.put("4",2);map.put("7",3);map.put("5",4);
    // [4=2, 5=4, 7=3, 8=1]
    System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList()));
    // [8=1, 4=2, 7=3, 5=4]
    System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()));
    // [8=1, 7=3, 5=4, 4=2]
    System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByKey((k1, k2) -> -k1.compareTo(k2))).collect(Collectors.toList()));
    // [5=4, 7=3, 4=2, 8=1]
    System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByValue((v1, v2) -> -v1.compareTo(v2))).collect(Collectors.toList()));
    map.put("9", null);
    // java.lang.NullPointerException
    System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()));
}
```

#### ==备注1 强转&==

> Comparator<Entry<K, V>> & Serializable表示同时强转类型成多个接口，相当于返回的类实现了Comparator和Serializable接口，多用于 lambda 表达式

## 四、方法




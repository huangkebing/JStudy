package apache.commons.lang3;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * org.apache.commons.lang3.ArrayUtils数据工具类使用示例
 * 其中部分方法和Arrays提供的方法重复(如toString())
 * 提供的一些方法，可以将数组像List那样使用
 */
public class ArrayUtilsTest {
    /**
     * 数组判空，针对null或者空的情况，以及将null转化为空数组
     */
    @Test
    public void emptyTest(){
        int[] array1 = {};
        System.out.println(ArrayUtils.isEmpty(array1));
        System.out.println(array1 == null || array1.length == 0);
        int[] array2 = null;
        System.out.println(ArrayUtils.isNotEmpty(array2));
        System.out.println(Arrays.toString(ArrayUtils.nullToEmpty(array2)));
        System.out.println(array2 != null && array2.length != 0);
    }

    /**
     * 数组元素查找下标
     * jdk中没有提供相关方法，如果想实现这些功能，需要自己使用循环遍历数组或者将数组转化为List
     */
    @Test
    public void searchTest(){
        int[] array = {1,1,2,3,5,8,13,21,34,55};
        // 寻找元素在数组中的下标，只取第一个
        System.out.println(ArrayUtils.indexOf(array, 1));
        // 从下标1(包括1)的元素开始查找，只取第一个
        System.out.println(ArrayUtils.indexOf(array, 1, 1));
        System.out.println(ArrayUtils.indexesOf(array, 1));
        // 寻找元素在数组中的下标，只取最后一个
        System.out.println(ArrayUtils.lastIndexOf(array, 1));
        // 判断数组中是否有该元素
        System.out.println(ArrayUtils.contains(array, 1));
    }

    @Test
    public void modifyTest(){
        int[] array = {1,1,2,3,5,8,13,21,34,55};
        //------------insert------------
        // 向数组尾部插入一个元素，会自动扩容，但和ArrayList不同，每次只会扩容1
        System.out.println(Arrays.toString(ArrayUtils.add(array, 89)));
        // 向数组首部插入一个元素
        System.out.println(Arrays.toString(ArrayUtils.addFirst(array, 0)));
        // 向指定位置插入任意数量的元素
        System.out.println(Arrays.toString(ArrayUtils.insert(2, array, 10, 11)));
        // 向数组尾部插入另一个数组
        int[] array2 = {89, 144, 233};
        System.out.println(Arrays.toString(ArrayUtils.addAll(array, array2)));
        //-----------remove------------
        // 切子数组，如下即下标在区间[0,5)内的元素
        System.out.println(Arrays.toString(ArrayUtils.subarray(array, 0, 5)));
    }

    /**
     * 一些其他的方法，简单介绍
     */
    @Test
    public void otherTest(){
        int[] array = {1,1,2,3,5,8,13,21,34,55};
        int[] array2 = {55,34,21,13,8,5,3,2,1,1};
        // 判断两个数组长度是否相等
        System.out.println(ArrayUtils.isSameLength(array, array2));
        // 判断数组内元素是否是按大小顺序排列的，提供的基本数据类型方法只能判断升序
        System.out.println(ArrayUtils.isSorted(array));
        System.out.println(ArrayUtils.isSorted(array2));
        // 可以自己定义排序方法，但需要将数组转化为包装类数组
        System.out.println(ArrayUtils.isSorted(ArrayUtils.toObject(array), (i1, i2) -> i2 - i1));
        System.out.println(ArrayUtils.isSorted(ArrayUtils.toObject(array2), (i1, i2) -> i2 - i1));
        // 数组倒序
        ArrayUtils.reverse(array);
        System.out.println(Arrays.toString(array));
        // 数组平移，offset为正数则向右平移，为负数则向左平移
        ArrayUtils.shift(array, -1);
        System.out.println(Arrays.toString(array));
        // 交换数组指定下标元素位置
        ArrayUtils.swap(array, 2,3);
        System.out.println(Arrays.toString(array));
        // 按每个数组元素生成随机数
        ArrayUtils.shuffle(array);
        System.out.println(Arrays.toString(array));
    }
}

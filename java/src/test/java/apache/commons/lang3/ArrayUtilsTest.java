package apache.commons.lang3;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * org.apache.commons.lang3.ArrayUtils数据工具类使用示例
 * 其中部分方法和Arrays提供的方法重复(如toString())
 */
public class ArrayUtilsTest {
    /**
     * 数组判空，针对null或者空的情况
     */
    @Test
    public void emptyTest(){
        int[] array1 = {};
        System.out.println(ArrayUtils.isEmpty(array1));
        System.out.println(array1 == null || array1.length == 0);
        int[] array2 = null;
        System.out.println(ArrayUtils.isNotEmpty(array2));
        System.out.println(array2 != null && array2.length != 0);
    }

    @Test
    public void searchTest(){
        int[] array = {1,1,2,3,5,8,13,21,34,55};
        // 寻找元素在数组中的下标，只取第一个
        System.out.println(ArrayUtils.indexOf(array, 1));
        // 从下标1(包括1)的元素开始查找，只取第一个
        System.out.println(ArrayUtils.indexOf(array, 1, 1));
        System.out.println(ArrayUtils.indexesOf(array, 1));
        System.out.println(ArrayUtils.indexesOf(array, 1, 1));

        System.out.println(ArrayUtils.contains(array, 1));
    }
}

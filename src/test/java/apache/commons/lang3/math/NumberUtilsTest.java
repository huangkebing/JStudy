package apache.commons.lang3.math;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

/**
 * apache.commons.lang3.math.NumberUtils类使用实例
 * 提供了一些对数字类的扩展方法
 */
public class NumberUtilsTest {
    /**
     * 判断是否是数字
     */
    @Test
    public void isNumberTest() {
        // 是否为数字，不支持十六进制和科学计数法(常用)
        System.out.println(NumberUtils.isParsable("3.14"));//true
        System.out.println(NumberUtils.isParsable("0X2F"));//false
        System.out.println(NumberUtils.isParsable("12345E-10"));//false
        // 判断str的每一个是否都是数字
        System.out.println(NumberUtils.isDigits("3.14"));//false
        System.out.println(NumberUtils.isDigits("-3"));//false
        System.out.println(NumberUtils.isDigits("30"));//true
        // 是否为数字，支持十六进制和科学计数法
        System.out.println(NumberUtils.isCreatable("3.14"));//true
        System.out.println(NumberUtils.isCreatable("0X2F"));//true
        System.out.println(NumberUtils.isCreatable("12345E-10"));//true
    }

    /**
     * max/min方法的扩展，支持多个数值同时比较
     * 以Integer为例
     */
    @Test
    public void maxMinTest() {
        // JDK方法
        System.out.println(Integer.max(3, 4));
        // NumberUtils
        System.out.println(NumberUtils.max(3, 4, 5));
        System.out.println(NumberUtils.min(3, 4, 5));
        System.out.println(NumberUtils.max(3, 4, 5, 6));
        int[] ints = {3, 4, 5, 6, 7, 8, 9};
        System.out.println(NumberUtils.max(ints));
    }

    /**
     * 字符串转化为数值类
     */
    @Test
    public void toNumberTest() {
        // createXXX，支持十六进制、八进制的数值转化;如Integer，即是在Integer.decode基础上加了一道判空
        System.out.println(NumberUtils.createInteger("0X2F"));
        System.out.println(NumberUtils.createInteger("0777"));
        System.out.println(Integer.decode("0X2F"));
        // 不为数值时，使用默认数值
        System.out.println(NumberUtils.toInt("abc", 0));
        int i;
        try {
            i = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            i = 0;
        }
        System.out.println(i);
    }
}

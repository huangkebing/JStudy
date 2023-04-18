package apache.commons.lang3;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;

public class StringUtilsTest {
    /**
     * 1. validate
     * 2. StringUtils
     * 3. concurrent包
     * https://xiaoxiaofeng.com/archives/stringutils
     */
    @Test
    public void test(){
        String test = "test";
        System.out.println(test.contains("ts"));
        System.out.println(StringUtils.contains(test, "ts"));
    }
    //---------------------判断与验证---------------------------
    /**
     * 判空方法
     * 1. isEmpty只判断字符串是否为空字符串或者null
     * 2. isBlank除空字符串和null外，还会判断空格、tab、换行符等
     * 3. 还有isNotEmpty和isNotBlank分别和isEmpty和isBlank对应
     * 4. isAllEmpty、isAllBlank、isNoneEmpty、isNoneBlank，支持多个字符串判空
     */
    @Test
    public void emptyOrBlankTest(){
        String empty1 = "";
        String empty2 = null;
        String empty3 = " \n    ";
        // isEmpty
        System.out.println(StringUtils.isEmpty(empty1));
        System.out.println(StringUtils.isEmpty(empty2));
        System.out.println(StringUtils.isEmpty(empty3));
        // isBlank
        System.out.println(StringUtils.isBlank(empty1));
        System.out.println(StringUtils.isBlank(empty2));
        System.out.println(StringUtils.isBlank(empty3));
        // isNoneEmpty
        String notEmpty = "Hello";
        System.out.println(StringUtils.isNoneEmpty(empty1, notEmpty));
    }

    /**
     * contains系列方法，StringUtils.contains()和jdk用法相同(多了一道null值校验)
     * 此外，还提供了一些contains的扩展方法
     */
    @Test
    public void containsTest(){
        String str = "Hello World!";
        // 忽略大小写的contains
        System.out.println(StringUtils.containsIgnoreCase(str, "hello"));
        // 判断是否包含空白字符，包括空格、tab、换行符等
        System.out.println(StringUtils.containsWhitespace(str));
        // 判断字符串是否只包含给定的字符，也可以给字符串
        System.out.println(StringUtils.containsOnly(str, 'H', 'e', 'l', 'o', ' ', 'W', 'r', 'd', '!'));
        // 判断字符串是否不包含给定的字符，也可以给字符串
        System.out.println(StringUtils.containsNone(str, "\n"));
        // 给多个字符串，判断是否包含其中任意一个
        System.out.println(StringUtils.containsAny(str, "hello", "Hello"));
    }

    /**
     * startsWith、endsWith系列方法
     */
    @Test
    public void startEndWithTest(){
        String str = "Hello World!";
        System.out.println(StringUtils.startsWithIgnoreCase(str, "hello"));
        System.out.println(StringUtils.startsWithAny(str, "Hello", "hello"));
        System.out.println(StringUtils.endsWithIgnoreCase(str, "world!"));
        System.out.println(StringUtils.endsWithAny(str, "World!", "world!"));
    }

    //---------------------处理字符串---------------------------
    /**
     * 去除首尾的空白字符，trim和jdk中类似，strip提供了一些扩展
     */
    @Test
    public void removeWhite(){
        String str = " Hello World!\u2009\u2009 ";
        String str2 = " Hello World!\u2009\u2009 ";
        // trim，调用了String中的trim，去除首位字符，chars <= 32
        System.out.println(StringUtils.trim(str));
        // strip，使用Character.isWhitespace方法判断空字符，能够去掉unicode的空白字符
        System.out.println(StringUtils.strip(str));
        // 去除空字符前为null，则返回null；去除后，若字符串为空，则返回null
        System.out.println(StringUtils.stripToNull(str));
        // 去除空字符前为null，则返回空字符串；去除后，若字符串为空，则返回空字符串
        System.out.println(StringUtils.stripToEmpty(str));
        // 支持去除首尾的指定字符串
        System.out.println(StringUtils.strip(str, " Hel"));
        // 去除首部的指定字符串
        System.out.println(StringUtils.stripStart(str, " "));
        // 去除尾部的指定字符串
        System.out.println(StringUtils.stripEnd(str, " "));
        // 批量去除，返回的是一个String数组
        System.out.println(Arrays.toString(StringUtils.stripAll(str, str2)));
    }

    /**
     * 其他的一些去除方法
     */
    @Test
    public void removeOther(){
        // chomp,去除结尾的一处换行符，包括三种情况 \r、\n、\r\n
        System.out.println(StringUtils.chomp("abc\r\n\r\n"));
        // 去除末尾的一个字符，\r\n会当成一个字符
        System.out.println(StringUtils.chop("1,2,3,"));
        // 去除非数字字符
        System.out.println(StringUtils.getDigits("1,2,3,"));
    }

    //---------------------查找字符---------------------------
    /**
     * indexOf系列方法，lastIndexOf也有相同的方法，以indexOf为例
     */
    @Test
    public void indexOfTest(){
        String str = "Hello World!";
        // 和jdk类似，查找字符在字符串中的位置
        System.out.println(StringUtils.indexOf(str, "o"));
        System.out.println(StringUtils.indexOf(str, "o", 6));
        // 忽略大小写版的indexOf
        System.out.println(StringUtils.indexOfIgnoreCase(str, "h"));
        // 查找指定字符出现指定次数时的下标
        System.out.println(StringUtils.ordinalIndexOf(str, "l", 3));
        // 查找给定的多个字符中，第一个出现的下标
        System.out.println(StringUtils.indexOfAny(str, "lo"));
        // 查找除了给定的多个字符之外的字符，第一个出现的下标
        System.out.println(StringUtils.indexOfAnyBut(str, "lo"));
    }

    //---------------------编辑字符串---------------------------





    @Test
    public void editTest(){
        String str = "Hello World!";
        // 移除remove操作
        System.out.println(StringUtils.remove(str, "l"));
        // 覆盖overlay操作，即将str中的[6,9)的字符替换为abc
        System.out.println(StringUtils.overlay(str, "abc", 6, 9));
        // 重复repeat操作
        System.out.println(StringUtils.repeat(str, 2));
        // 替换replace操作
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            StringUtils.replace(str,"H", "A");
        }
        long l2 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            str.replace("H","A");
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l2-l1);
        System.out.println(l3-l2);
    }
}

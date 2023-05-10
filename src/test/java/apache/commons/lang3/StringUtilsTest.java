package apache.commons.lang3;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;

public class StringUtilsTest {
    //---------------------判断与验证---------------------------
    /**
     * 判空方法
     * 1. isEmpty只判断字符串是否为空字符串或者null
     * 2. isBlank除空字符串和null外，还会判断空格、tab、换行符甚至包含了unicode格式的空字符，基于Character.isWhitespace
     * 3. 还有isNotEmpty和isNotBlank分别和isEmpty和isBlank对应
     * 4. isAllEmpty、isAllBlank、isNoneEmpty、isNoneBlank，支持多个字符串判空
     */
    @Test
    public void emptyOrBlankTest(){
        String empty1 = "";
        String empty2 = null;
        String empty3 = " \n    \u2009";
        // isEmpty
        System.out.println(StringUtils.isEmpty(empty1));//true
        System.out.println(StringUtils.isEmpty(empty2));//true
        System.out.println(StringUtils.isEmpty(empty3));//false
        // isBlank
        System.out.println(StringUtils.isBlank(empty1));//true
        System.out.println(StringUtils.isBlank(empty2));//true
        System.out.println(StringUtils.isBlank(empty3));//true
        // isNoneEmpty，比如有多个入参需要判断非空
        String notEmpty = "Hello";
        System.out.println(StringUtils.isNoneEmpty(empty1, notEmpty));//false
    }

    /**
     * contains系列方法，StringUtils.contains()和jdk用法相同(多了一道null值校验)
     * 此外，还提供了一些contains的扩展方法
     */
    @Test
    public void containsTest(){
        String str = "Hello World!";
        System.out.println(StringUtils.contains(str, "Hello"));
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

    /**
     * split系列方法,
     */
    @Test
    public void splitTest(){
        String str = "a.b.;.;e.f..g.h";
        // JDK, 按正则'.;'分割字符串 [a.b, , e.f..g.h]
        System.out.println(Arrays.toString(str.split(".;")));
        // 按照字符'.',';'来分割字符串 [a, b, e, f, g, h]
        System.out.println(Arrays.toString(StringUtils.split(str, ".;")));
        // 按照字符'.',';'来分割字符串，但会保留空字符[a, b, , , , e, f, , g, h]
        System.out.println(Arrays.toString(StringUtils.splitPreserveAllTokens(str, ".;")));
        // 按照字符串".;"分割字符串 [a.b, e.f..g.h]
        System.out.println(Arrays.toString(StringUtils.splitByWholeSeparator(str, ".;")));
        // 按照字符串".;"分割字符串，但会保留空字符 [a.b, , e.f..g.h]
        System.out.println(Arrays.toString(StringUtils.splitByWholeSeparatorPreserveAllTokens(str, ".;")));
        // 按照字符类型来分割，大写字母、小写字母、数字、空格、各类符号 [foo, 200, B, ar, BB, ar, !,  , ;]
        System.out.println(Arrays.toString(StringUtils.splitByCharacterType("foo200BarBBar! ;")));
        // 驼峰分割，大写字母后面是小写字母，则会分割到一起 [foo, 200, B, Bar, Bar, !,  , ;]
        System.out.println(Arrays.toString(StringUtils.splitByCharacterTypeCamelCase("foo200BBarBar! ;")));
    }

    /**
     * split方法效率测试，jdk中的split使用了正则，而StringUtils用的是字符串匹配
     */
    @Test
    public void splitCostTest(){
        String str = "Hello,World";
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            str.split(",");
        }
        long l2 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            StringUtils.split(str, ",");
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l2-l1);
        System.out.println(l3-l2);
    }

    /**
     * subString系列方法，subString方法和jdk使用方式相同
     * 还提供了一些快捷的切割方法
     */
    @Test
    public void subStringTest(){
        String str = "Hello World!";
        // 首部取5个字符
        System.out.println(StringUtils.left(str, 5));
        // 尾部取6个字符
        System.out.println(StringUtils.right(str, 6));
        // 从小标3开始的字符(包括)，取5个字符
        System.out.println(StringUtils.mid(str, 3, 5));
    }

    /**
     * 替换操作，String提供的方法使用了正则
     * 此外还提供了忽略大小写的替换
     */
    @Test
    public void replaceTest(){
        String str = "Hello World!";
        System.out.println(StringUtils.replace(str, "Hello", "hello"));
        System.out.println(StringUtils.replaceIgnoreCase(str, "hello", "hello"));
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

    /**
     * 其他字符串编辑操作，移除、覆盖、重复
     */
    @Test
    public void editTest(){
        String str = "Hello World!";
        // 移除remove操作
        System.out.println(StringUtils.remove(str, "l"));
        // 覆盖overlay操作，即将str中的[6,9)的字符替换为abc
        System.out.println(StringUtils.overlay(str, "abc", 6, 9));
        // 重复repeat操作
        System.out.println(StringUtils.repeat(str, 2));
    }
}

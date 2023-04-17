package apache.commons.lang3;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class StringUtilsTest {
    /**
     * 1. validate
     * 2. StringUtils
     * 3. objectUtils
     * 4. stream包
     * 5. concurrent包
     * https://xiaoxiaofeng.com/archives/stringutils
     */
    @Test
    public void test(){
        String str = "  Hello World! ";
        System.out.println(StringUtils.trim(str));
        System.out.println(StringUtils.strip(str, "ld! "));
        System.out.println(StringUtils.deleteWhitespace(str));
        System.out.println(StringUtils.normalizeSpace(str));
        String str1 = "\nHello World!\n";
        System.out.println(StringUtils.chomp(str1));
    }

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

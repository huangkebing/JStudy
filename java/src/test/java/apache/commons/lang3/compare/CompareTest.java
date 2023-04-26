package apache.commons.lang3.compare;

import org.apache.commons.lang3.compare.ComparableUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * apache.commons.lang3.compare.ComparableUtils类使用示例
 * 封装compareTo
 */
public class CompareTest {
    @Test
    public void compareTest() {
        BigDecimal a = new BigDecimal("3.14");
        BigDecimal b = new BigDecimal("5.14");
        BigDecimal c = new BigDecimal("3.14");
        ComparableUtils.ComparableCheckBuilder<BigDecimal> aCompare = ComparableUtils.is(a);
        // a在[b,c]或[c,b]区间内？
        System.out.println(aCompare.between(b, c));
        System.out.println(a.compareTo(c) >= 0 && a.compareTo(b) <= 0);
        // a在(b,c)或(c,b)区间内？
        System.out.println(aCompare.betweenExclusive(b, c));
        System.out.println(a.compareTo(c) > 0 && a.compareTo(b) < 0);
        // a等于b？
        System.out.println(aCompare.equalTo(b));
        System.out.println(a.compareTo(b) == 0);
        // a大于b？
        System.out.println(aCompare.greaterThan(b));
        System.out.println(a.compareTo(b) > 0);
        // a大于等于b？
        System.out.println(aCompare.greaterThanOrEqualTo(b));
        System.out.println(a.compareTo(b) >= 0);
        // a小于b？
        System.out.println(aCompare.lessThan(b));
        System.out.println(a.compareTo(b) < 0);
        // a小于等于b？
        System.out.println(aCompare.lessThanOrEqualTo(b));
        System.out.println(a.compareTo(b) <= 0);
    }

    @Test
    public void timeTest(){
        BigDecimal a = new BigDecimal("3.14");
        BigDecimal b = new BigDecimal("5.14");
        long l1 = System.currentTimeMillis();
        for (int j = 0; j < 1000000; j++) {
            boolean b1 = a.compareTo(b) > 0;
        }
        long l2 = System.currentTimeMillis();
        for (int j = 0; j < 1000000; j++) {
            boolean b2 = ComparableUtils.is(a).greaterThan(b);
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l2 - l1); //13ms
        System.out.println(l3 - l2); //41ms
        // 比较逻辑的封装，相比原生实现：
        // 可读性更强，原生compareTo需要和0比较
        // 但效率较差，每次执行ComparableUtils.is()都会创建一个对象
    }

    @Test
    public void functionTest() {
        List<BigDecimal> list = Arrays.asList(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"),
                new BigDecimal("4"), new BigDecimal("5"), new BigDecimal("6"));
        // 过滤出list中大于3.14的值
        System.out.println(list.stream().filter(ComparableUtils.gt(new BigDecimal("3.14"))).collect(Collectors.toList()));
        System.out.println(list.stream().filter(a -> a.compareTo(new BigDecimal("3.14")) > 0)
                .collect(Collectors.toList()));
        // 过滤出list中在[3.14,5.18]中的值
        System.out.println(list.stream().filter(ComparableUtils.between(new BigDecimal("3.14"), new BigDecimal("5.18")))
                .collect(Collectors.toList()));
    }
}

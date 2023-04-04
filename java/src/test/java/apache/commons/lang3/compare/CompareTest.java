package apache.commons.lang3.compare;

import org.apache.commons.lang3.compare.ComparableUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompareTest {
    @Test
    public void normalTest() {
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
    public void functionTest() {
        List<BigDecimal> list = Arrays.asList(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"),
                new BigDecimal("4"), new BigDecimal("5"), new BigDecimal("6"));
        // 过滤出list中大于3.14的值
        System.out.println(list.stream().filter(ComparableUtils.gt(new BigDecimal("3.14"))).collect(Collectors.toList()));
        System.out.println(list.stream().filter(a -> ComparableUtils.is(a).greaterThan(new BigDecimal("3.14")))
                .collect(Collectors.toList()));
        // 过滤出list中在[3.14,5.18]中的值
        System.out.println(list.stream().filter(ComparableUtils.between(new BigDecimal("3.14"), new BigDecimal("5.18")))
                .collect(Collectors.toList()));
    }
}

package apache.commons.lang3.math;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * apache.commons.lang3.math.Fraction类使用实例
 * 实现了分数，并提供了分数的各项运算
 */
public class FractionTest {
    @Test
    public void fractionTest(){
        // 2/6，分子分母必须为int
        Fraction f1 = Fraction.getFraction(2, 6);
        // 1/3，使用continued fraction algorithm算法，分母最大为10000
        Fraction f2 = Fraction.getFraction("0.3333333");
        // 3333/10000，和f2类似
        Fraction f3 = Fraction.getFraction(0.3333);
        // 生成带分数 2又1/3
        Fraction f4 = Fraction.getFraction(2, 1, 3);
        // 1/4，会进行约分操作
        Fraction f5 = Fraction.getReducedFraction(3, 12);
        // 加减乘除运算
        System.out.println(f1.add(f2).toProperString());
        System.out.println(f1.subtract(f3).toProperString());
        System.out.println(f1.multiplyBy(f4).toProperString());
        System.out.println(f1.divideBy(f5).toProperString());
        // 倒数、约分、绝对值、幂、取反
        System.out.println(f1.invert().toProperString());
        System.out.println(f1.reduce().toProperString());
        System.out.println(f1.abs().toProperString());
        System.out.println(f1.pow(2).toString());
        System.out.println(f1.negate().toProperString());
    }

    /**
     * 可以解决一些精度丢失的问题
     * 如计算两个均价的差值
     */
    @Test
    public void precisionTest() {
        BigDecimal cost1 = new BigDecimal("3451.21");
        BigDecimal count1 = new BigDecimal("110");
        BigDecimal cost2 = new BigDecimal("6553.21");
        BigDecimal count2 = new BigDecimal("70");
        BigDecimal avg1 = cost1.divide(count1, 4, RoundingMode.HALF_UP);
        BigDecimal avg2 = cost2.divide(count2, 4, RoundingMode.HALF_UP);
        //-62.2427
        System.out.println(avg1.subtract(avg2));
        Fraction cost3 = Fraction.getFraction("3451.21");
        Fraction count3 = Fraction.getFraction("110");
        Fraction cost4 = Fraction.getFraction("6553.21");
        Fraction count4 = Fraction.getFraction("70");
        Fraction avg3 = cost3.divideBy(count3);
        Fraction avg4 = cost4.divideBy(count4);
        //-62.2426
        System.out.println(BigDecimal.valueOf(avg3.subtract(avg4).doubleValue()).setScale(4, RoundingMode.HALF_UP));
    }

    /**
     * 简单效率测试
     */
    @Test
    public void costTest() {
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            BigDecimal cost1 = new BigDecimal("3451.21");
            BigDecimal count1 = new BigDecimal("110");
            BigDecimal cost2 = new BigDecimal("6553.21");
            BigDecimal count2 = new BigDecimal("70");
            BigDecimal avg1 = cost1.divide(count1, 4, RoundingMode.HALF_UP);
            BigDecimal avg2 = cost2.divide(count2, 4, RoundingMode.HALF_UP);
            avg1.subtract(avg2);
        }
        long l2 = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            Fraction cost3 = Fraction.getFraction("3451.21");
            Fraction count3 = Fraction.getFraction("110");
            Fraction cost4 = Fraction.getFraction("6553.21");
            Fraction count4 = Fraction.getFraction("70");
            Fraction avg3 = cost3.divideBy(count3);
            Fraction avg4 = cost4.divideBy(count4);
            BigDecimal.valueOf(avg3.subtract(avg4).doubleValue()).setScale(4, RoundingMode.HALF_UP);
        }
        long l3 = System.currentTimeMillis();
        System.out.println(l3 -l2);
        System.out.println(l2 -l1);
    }
}

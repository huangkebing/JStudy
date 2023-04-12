package apache.commons.lang3.math;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Test;

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
}

package apache.commons.lang3.math;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

public class NumberUtilsTest {
    @Test
    public void isNumberTest() {
        System.out.println(NumberUtils.isParsable("3.14"));
        System.out.println(NumberUtils.isParsable("3"));
        System.out.println(NumberUtils.isDigits("3.14"));
        System.out.println(NumberUtils.isDigits("3"));
        System.out.println(NumberUtils.isCreatable("3.14"));
        System.out.println(NumberUtils.isCreatable("3"));
    }
}

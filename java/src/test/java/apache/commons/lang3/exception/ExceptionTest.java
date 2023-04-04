package apache.commons.lang3.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

public class ExceptionTest {
    @Test
    public void test(){
        try {
            int i = 1 / 0;
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getMessage(e));
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }
}

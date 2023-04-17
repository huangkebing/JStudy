package apache.commons.lang3;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.Validate;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * 提供一些校验方法，如不符合验证条件，均为抛出IllegalArgumentException
 */
public class ValidateTest {


    /**
     * 空校验，此处以空list为例
     * 类似的方法还有：
     * Validate.notBlank(),通常用于校验空String
     * Validate.notNull(),校验null对象
     * Validate.notNaN(),校验Nan 浮点数
     */
    @Test
    public void emptyTest(){
        List<String> list = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> Validate.notEmpty(list));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validate.notEmpty(list, "入参%s为空", "list"));
        assertEquals("入参list为空", exception.getMessage());
    }

}

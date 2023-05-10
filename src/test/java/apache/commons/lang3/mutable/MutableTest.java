package apache.commons.lang3.mutable;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * apache.commons.lang3.mutable包测试
 * mutable包下有一些基本类型的可变包装类，跟据Mutable接口注释，有两个应用场景
 */
public class MutableTest {

    /**
     * 场景1，如果方法入参为不可变对象，而方法中需要修改入参且外部方法需要感知时
     */
    @Test
    public void methodParamTest(){
        // 通过方法的return重新赋值
        String param = "outside";
        String param1 = editParam(param);
        System.out.println(param);
        System.out.println(param1);
        // 使用MutableObject
        MutableObject<String> mutableParam = new MutableObject<>(param);
        editMutableParam(mutableParam);
        System.out.println(mutableParam.getValue());
    }

    public String editParam(String param){
        param = "inside";
        return param;
    }

    public void editMutableParam(MutableObject<String> param){
        param.setValue("inside");
    }

    /**
     * 场景2，在集合中使用基本数据类型时，若值需要经常变动，可以使用可变对象
     * 和在循环中执行字符串+=操作类似
     */
    @Test
    public void collectionTest(){
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("key", 128);
        for (int i = 0; i < 3; i++) {
            map1.put("key", map1.get("key") + 1);
            System.out.println(System.identityHashCode(map1.get("key")));
        }
        System.out.println(map1.get("key"));
        Map<String, MutableInt> map2 = new HashMap<>();
        map2.put("key", new MutableInt(128));
        for (int i = 0; i < 3; i++) {
            map2.get("key").increment();
            System.out.println(System.identityHashCode(map2.get("key")));
        }
        System.out.println(map2.get("key"));
    }
}

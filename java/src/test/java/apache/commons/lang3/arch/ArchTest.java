package apache.commons.lang3.arch;

import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.arch.Processor;
import org.junit.Test;

/**
 * apache.commons.lang3.arch.ArchUtils类使用示例
 * 一些获取CPU信息的方法，基于os.arch系统变量
 */
public class ArchTest {
    @Test
    public void archTest() {
        // 获取本机的CPU信息
        Processor equip = ArchUtils.getProcessor();
        // 获取指定CPU信息
        Processor x86 = ArchUtils.getProcessor("x86");
        Processor ppc64 = ArchUtils.getProcessor("ppc64");
        // 获取CPU的位数
        System.out.println(ppc64.getArch());
        // 获取CPU的类型
        System.out.println(ppc64.getType());
        // 判断CPU是否为32位/64位
        System.out.println(ppc64.is32Bit());
        System.out.println(ppc64.is64Bit());
        // 判断CPU是否为IA64/PPC/X86
        System.out.println(ppc64.isIA64());
        System.out.println(ppc64.isPPC());
        System.out.println(ppc64.isX86());
    }
}

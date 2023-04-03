package apache.commons.lang3.arch;

import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.arch.Processor;
import org.junit.Test;

public class ArchTest {
    @Test
    public void test() {
        Processor processor = ArchUtils.getProcessor("amd64");
        System.out.println(processor.getArch());
        System.out.println(processor.getType());
        System.out.println(processor.is32Bit());
        System.out.println(processor.is64Bit());
        System.out.println(processor.isIA64());
        System.out.println(processor.isPPC());
        System.out.println(processor.isX86());
    }
}

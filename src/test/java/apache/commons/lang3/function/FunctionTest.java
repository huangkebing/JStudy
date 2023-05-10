package apache.commons.lang3.function;

import org.apache.commons.lang3.function.Failable;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * apache.commons.lang3.compare.Failable类使用实例
 * 如FailableConsumer和Consumer相比，区别在于FailableConsumer允许抛出checked异常，而Consumer必须在方法内使用try-catch处理checked异常
 */
public class FunctionTest {
    @Test
    public void test(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\work\\function.txt"));
            list.forEach(Failable.asConsumer(i -> {
                writer.write(String.valueOf(i));
                writer.flush();
            }));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void normalTest(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\work\\function.txt"));
            list.forEach(i -> {
                try {
                    // IOException为checked异常，Consumer.accept方法无法抛出异常，只能在内部捕获
                    writer.write(String.valueOf(i));
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void forTest(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\work\\function.txt"));
            for (Integer i : list) {
                writer.write(String.valueOf(i));
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

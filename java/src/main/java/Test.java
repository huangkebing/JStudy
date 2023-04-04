import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public class Test {
    public static void main(String[] args) {
        IntBinaryOperator operator = (int a, int b) -> {
            int i = 0;
            try {
                i = a / b;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return i;
        };
        try {
            operator.applyAsInt(1,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

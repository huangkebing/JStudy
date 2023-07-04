package sort;

import java.util.Arrays;

public class Console {
    public static void main(String[] args) {
        int[] array = {1,2,3,8,4,5,6,7};
        Merge.mergeSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }
}

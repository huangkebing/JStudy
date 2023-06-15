package sort;

/**
 * 插入排序
 */
public class Insertion {
    public static int[] insertionSort(int[] array) {
        /*
         * 从1开始循环，因为第一个元素可以认为是默认排序的
         */
        for (int i = 1; i < array.length; i++) {
            int lastIndex = i - 1;
            int curr = array[i];
            /*
             * 从已排序的末尾开始往前遍历，找到位置插入
             */
            for (int j = lastIndex; j >= 0; j--) {
                if (array[j] > curr) {
                    array[j + 1] = array[j];
                } else {
                    array[j + 1] = curr;
                    break;
                }
            }
        }
        return array;
    }
}

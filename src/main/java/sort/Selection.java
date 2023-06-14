package sort;

/**
 * 选择排序
 */
public class Selection {
    public static int[] selectionSort(int[] array) {
        /*
         * 外层排序只需执行length-1次
         */
        for (int i = 0; i < array.length - 1; i++) {
            // 取剩下元素中的极值
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            // 移动到剩下元素的首部
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
        return array;
    }
}

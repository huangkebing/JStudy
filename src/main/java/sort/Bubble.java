package sort;

/**
 * 冒泡排序
 */
public class Bubble {
    public static int[] bubbleSort(int[] array) {
        /*
         * 外层循环每执行一次，排序好的元素数量便+1，i=已排序元素数量-1
         * 此处i从1开始，因为执行length-1轮时，未排序数量=length - (length - 1 - 1) = 2
         * 所以length-1轮就是最后一轮
         */
        for (int i = 1; i < array.length; i++) {
            /*
             * 内层循环每执行一次，就比较相邻两个元素的大小关系
             * array[j] > array[j + 1]是实现升序的逻辑，改成小于号则为降序
             * 此处循环上限是length-i，因为要使用j+1为了避免越界，需要在已排序元素数量上多减1
             */
            boolean swap = false;
            for (int j = 0; j < array.length - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                    swap = true;
                }
            }
            /*
             * 当一轮比较完成，没有元素的交换，说明排序已经完成，提前终止
             */
            if(!swap){
                break;
            }
        }
        return array;
    }
}

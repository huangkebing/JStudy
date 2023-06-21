package sort;

public class Quick {
    public static int[] quickSort(int[] array) {
        quick(array, 0, array.length - 1);
        return array;
    }

    private static void quick(int[] array, int left, int right) {
        if (left >= right) {
            return;
        }
        int partition = partition(array, left, right);
        quick(array, left, partition - 1);
        quick(array, partition + 1, right);
    }

    public static int partition(int[] array, int start, int end) {
        int i = start;
        int key = array[start];
        while (start < end) {
            while (start < end && array[end] >= key) {
                end--;
            }
            while (start < end && array[start] <= key) {
                start++;
            }
            swap(array, start, end);
        }
        swap(array, start, i);
        return start;
    }

    public static void swap(int[] array, int n, int m) {
        int temp = array[n];
        array[n] = array[m];
        array[m] = temp;
    }
}

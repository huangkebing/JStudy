public class Test {

    public static void main(String[] args) {
        int[] postorder = {1,2,5,10,6,9,4,3};
        Test test = new Test();
        System.out.println(test.verifyPostorder(postorder));
    }

    public boolean verifyPostorder(int[] postorder) {
        return checkPostorder(postorder, 0, postorder.length - 1);
    }

    private boolean checkPostorder(int[] postorder, int begin, int end) {
        if (begin == end) {
            return true;
        }
        int root = postorder[end];
        int rightIndex = 0;
        for (int i = end - 1; i >= 0; i--) {
            if (postorder[i] < root) {
                rightIndex = i + 1;
                break;
            }
        }
        for (int i = begin; i < rightIndex; i++) {
            if (postorder[i] > root) {
                return false;
            }
        }
        if(begin == rightIndex){
            return checkPostorder(postorder, rightIndex, end - 1);
        } else if(end == rightIndex){
            return checkPostorder(postorder, begin, rightIndex - 1);
        }
        return checkPostorder(postorder, begin, rightIndex - 1) && checkPostorder(postorder, rightIndex, end - 1);
    }
}

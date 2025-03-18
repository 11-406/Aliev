public class Main {
    public static void main(String[] args) {
        int[] A = {0, 1, 2};
        int[] nums = new int[4];
        for(int i = 0; i < A.length; i++) {
            nums[i] = 0;
        }

        for(int i = 0; i < A.length; i++) {
            nums[A[i]] = 1;
        }
        int cnt = 0;
        for (int i = 0; i < nums.length; i++) {
            if(nums[i] == 0) {
                cnt = i;
            }
        }
        System.out.println(cnt);
    }
}
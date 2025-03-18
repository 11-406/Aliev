public class Main {
    public static void main(String[] args) {
        int n = 6;
        int[] arr = new int[n];
        arr[0] = 1;
        arr[1] = 1;
        for(int i = 2; i < n; i++) {
            arr[i] = arr[i-1] + arr[i-2];
        }
        System.out.println(arr[n-1]);
    }
}
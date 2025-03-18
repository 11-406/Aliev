
public class Main {
    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3};
        int len = arr.length;
        int s = 0;
        for (int i = 0; i < arr.length; i++) {
            s += arr[i];
        }
        System.out.println((len*(len+0))/2 - s);
    }
}
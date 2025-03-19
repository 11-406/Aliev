import java.sql.SQLOutput;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int p = 11;
        System.out.println(alg1(p));
        System.out.println(alg2(p));

    }


    public static boolean alg1(int p) {
        if(p % 2 == 0) {
            return false;
        }
        for(int i = 3; i*i <= p; i++) { // O(n^1/2)
            if(p % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean alg2(int p) {
        Random r = new Random(); // O((logn)^3)
        boolean flag = true;
        for(int i = 0; i < 10; i++) {
            int n = r.nextInt(1,p);
            if((Math.pow(n, p-1) % p) == 1) {
                flag = true;
            }else {
                flag = false;
                break;
            }

        }
        return flag;

    }


    public static int getPow(int x, int p) {
        int res = 1;
        while(p > 0) {
            if(p % 2 != 0) {
                res = res * x;
            }
            x *= x;
            p /= 2;
        }
        return res;
    }
}
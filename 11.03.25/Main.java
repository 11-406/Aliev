public class Main {
    public static void main(String[] args) {
        double starts = System.nanoTime();
        int z = 50;
        int base = 1;
        int mult = 2;
        for (int i=0;i<z;i++){
            base*=mult;
        }
        System.out.println(base);
        double ends = System.nanoTime();
        double ressss = (ends-starts)/1000;
        System.out.println(ressss);
        double start = System.nanoTime();
        int res = 1;
        int x = 2;
        int p = 50;
        while(p>0){
            if (p%2!=0){
                res = res*x;
                x*=x;
                p/=2;
            }else {
                x*=x;
                p/=2;
            }
        }
        System.out.println(res);
        double end = System.nanoTime();
        double resss = (end-start)/1000;
        System.out.println(resss);
        System.out.println(5/2);
    }
}
public class Traversal {
    static int maxNumb;
    Node root;

    public Traversal(Node root) {
        this.root = root;
        maxNumb = root.d;
    }
    public static void order(Node root) {
        if(root == null) {
            return;
        }
        if(maxNumb < root.d) {
            maxNumb = root.d;
        }
        System.out.print(root.d);
        order(root.left);
        order(root.right);//найти макс элемент в дереве(ДЗ)
    }

    public static void getMaxNumb() {
        System.out.println("\n" + maxNumb);;
    }
}

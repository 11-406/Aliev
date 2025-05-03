public class Main {
    public static void main(String[] args) {
        Node r = new Node(1);
        r.left = new Node(2);
        r.right = new Node(3);
        r.left.left = new Node(4);
        r.left.right = new Node(5);
        Traversal traversal = new Traversal(r);
        traversal.order(r);
        traversal.getMaxNumb();
    }
}
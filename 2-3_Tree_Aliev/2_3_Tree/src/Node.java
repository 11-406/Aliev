import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Integer> keys;
    public List<Node> children;
    public Node parent;
    public boolean isLeaf;

    public Node() {
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.parent = null;
        this.isLeaf = true;
    }
}
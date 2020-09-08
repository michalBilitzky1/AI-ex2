
import java.util.HashMap;


public class Node {

    private String data;
    private HashMap<String,Node> children=new HashMap<>();


    private Node parent = null;

    public Node(String data) {
        this.data = data;

    }

    public Node addChild(String branch,Node child) {
        child.setParent(this);
        this.children.put(branch,child);
        return child;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }



    public HashMap<String,Node> getChildren() {
        return children;
    }

    public String getData() {
        return data;
    }

      private void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }



}

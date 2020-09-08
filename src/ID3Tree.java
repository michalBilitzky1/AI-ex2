import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ID3Tree {

    private PrintWriter writer;
    private Node root;
    private List<String> printTree;
    private List<Node> temp;


    public ID3Tree() {
        this.printTree = new ArrayList<String>();
        this.temp=new ArrayList<>();

    }


    public void setRoot(Node root) {
        this.root = root;
        this.temp.add(root);
    }


    public Node getRoot() {
        return this.root;
    }


    public void printTreeToFile() {
        try {
            this.writer = new PrintWriter("output_tree.txt");
            recursiveWrite(root, 0);
            if (printTree.get(printTree.size()-1).equals("\n")){
                printTree.remove(printTree.size()-1);
            }
            for (int i = 0; i < printTree.size(); i++) {
                writer.print(printTree.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (this.writer != null) {
                writer.close();
            }
        }
    }


    public void recursiveWrite(Node node, int depth) {
        if (node.isLeaf()) {
            printTree.add(":"+node.getData()+"\n");

        } else {
            SortedSet<String> sortedChildren = new TreeSet<>(node.getChildren().keySet());
            for (String branch : sortedChildren) {
                for (int i = 0; i < depth; i++) {
                    printTree.add("\t");
                    this.temp.add(new Node(branch));
                    // this.temp.setData(branch);


                }

                if (node.getParent()!=null) {
                    printTree.add("|");
                }
                printTree.add(node.getData()+ "=" + branch);

                Node childNode = node.getChildren().get(branch);
                if (!childNode.isLeaf()) {
                    printTree.add("\n");
                }
                recursiveWrite(childNode, depth + 1);
            }
        }
    }





}

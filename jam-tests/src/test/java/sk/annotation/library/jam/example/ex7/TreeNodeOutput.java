package sk.annotation.library.jam.example.ex7;

public class TreeNodeOutput implements TreeNode {
    private String data;
    private TreeNodeOutput leftChild;
    private TreeNodeOutput rightChild;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TreeNodeOutput getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(TreeNodeOutput leftChild) {
        this.leftChild = leftChild;
    }

    public TreeNodeOutput getRightChild() {
        return rightChild;
    }

    public void setRightChild(TreeNodeOutput rightChild) {
        this.rightChild = rightChild;
    }
}

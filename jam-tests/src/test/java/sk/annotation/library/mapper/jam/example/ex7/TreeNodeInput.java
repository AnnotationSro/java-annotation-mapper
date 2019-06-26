package sk.annotation.library.mapper.jam.example.ex7;

public class TreeNodeInput implements TreeNode {
    private String data;
    private TreeNodeInput leftChild;
    private TreeNodeInput rightChild;

    public TreeNodeInput() {
    }

    public TreeNodeInput(String data, TreeNodeInput leftChild, TreeNodeInput rightChild) {
        this.data = data;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    @Override
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public TreeNodeInput getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(TreeNodeInput leftChild) {
        this.leftChild = leftChild;
    }

    @Override
    public TreeNodeInput getRightChild() {
        return rightChild;
    }

    public void setRightChild(TreeNodeInput rightChild) {
        this.rightChild = rightChild;
    }
}

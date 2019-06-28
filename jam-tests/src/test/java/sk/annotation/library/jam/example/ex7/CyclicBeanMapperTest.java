package sk.annotation.library.jam.example.ex7;

import org.junit.Test;
import sk.annotation.library.jam.utils.MapperInstanceUtil;

import static org.junit.Assert.*;

public class CyclicBeanMapperTest {
    @Test
    public void testCyclicBeanMapper(){
        TreeNodeInput inputTree = new TreeNodeInput("ROOT",
                new TreeNodeInput("L",
                        new TreeNodeInput("LL", null, null),
                        new TreeNodeInput("LR", null, null)),
                new TreeNodeInput("R",
                        new TreeNodeInput("RL",
                                new TreeNodeInput("RLL", null, null),
                                null),
                        null));

        CyclicBeanMapper mapper = MapperInstanceUtil.getMapper(CyclicBeanMapper.class);

        TreeNodeOutput outputTree = mapper.toOutput(inputTree);

        assertNotNull(outputTree);
        assertTrue(treesEqual(inputTree, outputTree));
    }

    private boolean treesEqual(TreeNode node1, TreeNode node2){
        if (node1 == null && node2 == null) return true;

        if (node1 != null && node2 != null) {
            boolean dataEqual = node1.getData() == null ? node2.getData() == null : node1.getData().equals(node2.getData());

            return dataEqual
                    && treesEqual(node1.getLeftChild(), node2.getLeftChild())
                    && treesEqual(node1.getRightChild(), node2.getRightChild());
        }

        return false;
    }
}

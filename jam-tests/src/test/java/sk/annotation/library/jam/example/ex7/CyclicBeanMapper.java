package sk.annotation.library.jam.example.ex7;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface CyclicBeanMapper {
    TreeNodeOutput toOutput(TreeNodeInput treeNodeInput);
}

package sk.annotation.library.mapper.jam.example.ex7;

import sk.annotation.library.mapper.jam.annotations.JamMapper;

@JamMapper
public interface CyclicBeanMapper {
    TreeNodeOutput toOutput(TreeNodeInput treeNodeInput);
}

package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This generic will represent Tree data structure
 * Created by idanp on 2/11/2015.
 */
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class TreeNode<T> {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private Tree tree;


    @JsonCreator
    public TreeNode(@JsonProperty("data") T data,
                    @JsonProperty("children") List<TreeNode<T>> children) {
        this.data = data;
        this.children = new ArrayList<>();
        if (children != null) {
            this.children = children;
            this.children.forEach(child -> {
                child.setParent(this);
            });
        }
    }

    public boolean isRoot() {
        return parent == null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }


    public Stream<TreeNode<T>> getAncestorsStream() {
        return Stream.concat(
                Stream.of(parent).filter(Objects::nonNull),
                Stream.of(parent).filter(Objects::nonNull).flatMap(TreeNode::getAncestorsStream)
        );
    }



    /**
     * @return stream of current node and descendants
     */
    public Stream<TreeNode<T>> getDescendantStreamIncludingCurrentNode() {
        return Stream.concat(
                Stream.of(this),
                children.stream().flatMap(TreeNode::getDescendantStreamIncludingCurrentNode));
    }

    /**
     * @param conditionStopFunc conditionStopFunc
     * @return stream descendants without current node
     */
    public Stream<TreeNode<T>> getDescendantStreamNotIncludingCurrentNode(Function<T, Boolean> conditionStopFunc) {
        return Stream.concat(
                children.stream().filter(e -> !conditionStopFunc.apply(e.getData())).flatMap(e -> e.getDescendantStreamNotIncludingCurrentNode(conditionStopFunc)),
                children.stream().filter(e -> conditionStopFunc.apply(e.getData()))).filter(e -> conditionStopFunc.apply(e.getData()));
    }

}

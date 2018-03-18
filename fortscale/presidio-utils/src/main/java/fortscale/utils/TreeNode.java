package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.function.Function;

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
            this.children.forEach(child -> {child.setParent(this);});
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

    public Set<TreeNode<T>> getAncestors() {
        AncestorsIterator<T> ancestorsIterator = new AncestorsIterator<T>(this);

        Set<TreeNode<T>> ancestors = new HashSet<>();
        while(ancestorsIterator.hasNext()){
            TreeNode<T> parent = ancestorsIterator.next();
            ancestors.add(parent);
        }

        return ancestors;
    }


    public DescendantIterator<T> getDescendantIterator() {
        return  new DescendantIterator<T>(this);
    }


    public DescendantIterator<T> getDescendantIterator(Function<T, Boolean> conditionStopFunc ) {
        DescendantIterator<T> descendantIterator = new DescendantIterator<T>(this);
        descendantIterator.setConditionStopFunc(conditionStopFunc);
        return  descendantIterator;
    }


}

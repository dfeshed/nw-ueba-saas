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
public class TreeNode<T> implements Iterable<TreeNode<T>> {

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
        ParentIterator<T> parentIterator = new ParentIterator<T>(this);

        Set<TreeNode<T>> ancestors = new HashSet<>();
        while(parentIterator.hasNext()){
            TreeNode<T> parent = parentIterator.next();
            ancestors.add(parent);
        }

        return ancestors;
    }

    public Iterator<TreeNode<T>> iterator() {
        Iterator<TreeNode<T>> iter = this.children.iterator();
        return iter;
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

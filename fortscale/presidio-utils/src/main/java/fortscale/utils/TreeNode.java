package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

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
    private Set<TreeNode<T>> ancestors;
    private boolean visited;

    @JsonCreator
    public TreeNode(@JsonProperty("data") T data,
                    @JsonProperty("children") List<TreeNode<T>> children) {
        this.data = data;
        ancestors = new HashSet<>();
        this.children = new ArrayList<>();
        if (children != null) {
            this.children = children;
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
        return ancestors;
    }

    public void setAncestors(Set<TreeNode<T>> ancestors) {
        this.ancestors = ancestors;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Iterator<TreeNode<T>> iterator() {
        Iterator<TreeNode<T>> iter = this.children.iterator();
        return iter;
    }

}

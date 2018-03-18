package fortscale.utils;


import java.util.*;
import java.util.function.Function;

public class DescendantIterator<T> implements Iterator<TreeNode<T>> {

    private TreeNode<T> treeNode;
    private Queue<TreeNode<T>> queue;
    private Function<T, Boolean> conditionStopFunc;
    private Boolean useStopCondition;
    private TreeNode<T> next;


    public DescendantIterator(TreeNode<T> treeNode) {
        this.treeNode = treeNode;
        this.queue = new LinkedList<>();
        this.useStopCondition = false;
        initiation(treeNode);

    }

    public void setConditionStopFunc(Function<T, Boolean> conditionStopFunc) {
        this.useStopCondition = true;
        this.conditionStopFunc = conditionStopFunc;
    }


    @Override
    public boolean hasNext() {
        Boolean result = false;
        if (!this.queue.isEmpty()) {
            if (useStopCondition) {
                result = hasNextWithCondition();
            } else {
                TreeNode<T> next = queue.remove();
                initiation(next);
                this.next = next;
                result = true;
            }
        }

        return result;
    }


    /**
     * check if tree node has next descendant due to stop condition
     * @return Boolean
     */
    private Boolean hasNextWithCondition() {
        Boolean result;
        do {
            TreeNode<T> next = queue.remove();
            result = conditionStopFunc.apply(next.getData());
            if (result) {
                this.next = next;
                break;
            } else {
                initiation(next);
            }
        } while (!this.queue.isEmpty());
        return result;
    }

    @Override
    public TreeNode<T> next() {
        return next;
    }


    private void initiation(TreeNode<T> treeNode) {
        queue.addAll(treeNode.getChildren());
    }
}

package fortscale.utils;


import java.util.*;
import java.util.function.Function;

/**
 * DescendantIterator get treeNode and return iterate over all descendants by default (useStopCondition=false).
 * DescendantIterator enable to define stop condition function
 * and then it iterate over descendants until the following condition are met.
 *
 * @param <T>
 */
public class DescendantIterator<T> implements Iterator<TreeNode<T>> {

    private TreeNode<T> treeNode;
    private Queue<TreeNode<T>> queue;
    private Function<T, Boolean> stopConditionFunc;
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
        this.stopConditionFunc = conditionStopFunc;
    }

    //todo: hasNext should not move the cursor. This code should be in the next() function.
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
     * and set it to next field
     * @return Boolean
     */
    private Boolean hasNextWithCondition() {
        Boolean result;
        do {
            TreeNode<T> next = queue.remove();
            result = stopConditionFunc.apply(next.getData());
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

package fortscale.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * AncestorsIterator get treeNode and return iterate over all ancestors
 * @param <T>
 */
public class AncestorsIterator<T> implements Iterator<TreeNode<T>> {

	private TreeNode<T> treeNode;
	private TreeNode<T> currentTreeNode;

	public AncestorsIterator(TreeNode<T> treeNode){
		this.treeNode = treeNode;
		this.currentTreeNode = treeNode;
	}

	@Override
	public boolean hasNext() {
		return this.currentTreeNode.getParent() != null;
	}

	@Override
	public TreeNode<T> next() {
		TreeNode<T> next = currentTreeNode.getParent();
		this.currentTreeNode = next;
		return next;
	}

}
